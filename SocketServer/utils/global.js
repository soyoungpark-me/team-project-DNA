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
redis.flushdb(() => {
  console.log("[ Redis ] session datas in Redis are removed Successfully ...");
});

/* mongodb */
const mongoose = require('mongoose');

const url = `mongodb://${process.env.EC2_HOST}:${process.env.MONGO_PORT}/${process.env.DB_NAME}`;
const testUrl = `mongodb://${process.env.EC2_HOST}:${process.env.MONGO_PORT}/DNA_stress`;

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
  const messageModel = testMongo.model("messages", 
    require("../schemas/TestSchema").createSchema(mongoose));
  testMongo["messageModel"] = messageModel;
  
  testMongo.collection("messages").deleteMany({},function(err){
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
  if (conn) {
    conn.createChannel(function(err, ch) {
      rabbitMQ.channel = ch;

      const ex = 'push';
      ch.assertExchange(ex, 'direct', {durable: false});
    });
  }
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

module.exports.redis = redis;
module.exports.pub = pub;
module.exports.sub = sub;
module.exports.mongo = mongo;
module.exports.testMongo = testMongo;
module.exports.rabbitMQ = rabbitMQ;
module.exports.logger = logger;