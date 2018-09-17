/******************************************************************************
' 파일     : global.js
' 작성     : 박소영
' 목적     : DB나 메시지 큐, logger 등 전역으로 쓰이는 모듈을 모아놓은 파일입니다.
******************************************************************************/

const config = require('../utils/config');

/* redis */
const redis = require('redis').createClient(process.env.REDIS_PORT, process.env.EC2_HOST);
redis.auth(process.env.REDIS_PASSWORD);
const pub = require('redis').createClient(process.env.REDIS_PORT, process.env.EC2_HOST);
pub.auth(process.env.REDIS_PASSWORD);
const sub = require('redis').createClient(process.env.REDIS_PORT, process.env.EC2_HOST);
sub.auth(process.env.REDIS_PASSWORD);

// 먼저 세션과 관련된 redis 데이터를 모두 초기화해줍니다.
redis.flushdb((err, result) => {
  console.log("[ Redis ] session datas in Redis are removed Successfully ...");
});

/* mysql */
const mysql = require('mysql');
const dbConfig = {
  host: process.env.MYSQL_HOST,
  port: process.env.MYSQL_PORT,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.DB_NAME
};

let connection = mysql.createConnection(dbConfig);

connection.connect(function(err){
  if (err) {
      console.log("[ MYSQL ] Cannot establish a connection with the database MySQL ... ");
      connection = reconnect(connection);
  } else {
      console.log("[ MYSQL ] *** New connection established with the database MySQL ...")
  }
});

function reconnect(connection){
  console.log("[ MYSQL ] New connection tentative ...");

  if (connection) connection.destroy(); // 현재 커넥션이 존재한다면 끊고 새로 만듭니다.
  connection = mysql.createConnection(dbConfig);

  connection.connect(function(err){
      if (err) setTimeout(reconnect, 2000); // 2초마다 연결을 요청합니다.
      else {
          console.log("[ MYSQL ] *** New connection established with the database ... ")
          return connection;
      }
  });
}

connection.on('disconnected', function(){
  console.log("[ MySQL ] Connection disconnected with the database MySQL ...");
});

connection.on('error', function(err) {
  if(err.code === "PROTOCOL_CONNECTION_LOST"){ // 서버 측에서 연결을 끊은 경우
    console.log("[ MYSQL ] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_QUIT"){ // 커넥션이 강제로 끊긴 경우
    console.log("[ MYSQL ] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_FATAL_ERROR"){ // Fatal error 발생
    console.log("[ MYSQL ] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_HANDSHAKE_TWICE"){ // 이미 커넥션이 존재하는 경우
    console.log("[ MYSQL ] !!! Cannot establish a connection with the database : ("+err.code+")");
  }
  else{ // etc
    console.log("[ MYSQL ] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
});

setInterval(function () {
    connection.query('SELECT 1');
}, 5000);


/* mongodb */
const mongoose = require('mongoose');

const url = `mongodb://${process.env.EC2_HOST}:${process.env.MONGO_PORT}/${process.env.DB_NAME}`;
const testUrl = `mongodb://${process.env.EC2_HOST}:${process.env.MONGO_PORT}/test`;

const options = {
  user: process.env.MONGO_USERNAME,
  pass: process.env.MONGO_PASSWORD,
  autoReconnect: true,
  useNewUrlParser: true,
  poolSize: 2,
  keepAlive: 300000,
  connectTimeoutMS: 30000,
  reconnectTries: 300000,
  reconnectInterval: 2000,
  promiseLibrary: global.Promise
};

mongoose.connect(url, options);

const mongo = {};
mongo.db = mongoose.connection;
mongo.db.on('error', console.error);
mongo.db.once('open', function(){
  console.log("[MongoDB] *** New connection established with the MongoDB ...");
  createSchema(config); // utils/config에 등록된 스키마 및 모델 객체 생성
});
mongo.db.on('disconnected', function(){
  console.log("[MongoDB] Connection disconnected with the MongoDB ...");
});

let testMongo = mongoose.createConnection(testUrl, options);

// test에도 스키마와 모델 객체 생성해주기
testMongo.once('open', function() {
  console.log("[MongoDB] *** New connection established with the *** TEST *** MongoDB ...");
  const testModel = testMongo.model("testModel", 
    require("../schemas/TestSchema").createSchema(mongoose));
  testMongo["testModel"] = testModel;
  
  testMongo.collection("messagemodels").deleteMany({},function(err){
    console.log("[MongoDB] dummy messages in test DB are removed Successfully ...");
  });
});

// config에 정의한 스키마 및 모델 객체 생성
function createSchema(config){
  const schemaLen = config.db_schemas.length;

  for (let i = 0; i < schemaLen; i++){
    let curItem = config.db_schemas[i];

    // 모듈 파일에서 모듈을 불러온 후 createSchema() 함수 호출!
    let curSchema = require(curItem.file).createSchema(mongoose);

    // User 모델 정의
    let curModel = mongoose.model(curItem.collection, curSchema);

    // database 객체에 속성으로 추가
    mongo[curItem.schemaName] = curSchema;
    mongo[curItem.modelName] = curModel;
    console.log("[MongoDB] { %s, %s } is added to mongo Object.",
      curItem.schemaName, curItem.modelName);
  }
};

/* RabbitMQ */
const rabbitMQ = require('amqplib/callback_api');
rabbitMQ.channel = '';

rabbitMQ.connect('amqp://localhost', function(err, conn) {
  conn.createChannel(function(err, ch) {
    rabbitMQ.channel = ch;

    const ex = 'push';
    ch.assertExchange(ex, 'direct', {durable: false});
  });
});

/* winston */
const winston = require('winston');
const logger = winston.createLogger({
  transports: [
    new (winston.transports.Console)({
      colorize: true,
      level: 'error'
    }),
    new (winston.transports.File)({
      level: 'error',
      filename: './test/socket-err.log'    
    })
  ],
  exitOnError: false,
});

module.exports.mysql = connection;
module.exports.redis = redis;
module.exports.pub = pub;
module.exports.sub = sub;
module.exports.mongo = mongo;
module.exports.testMongo = testMongo;
module.exports.rabbitMQ = rabbitMQ;
module.exports.logger = logger;