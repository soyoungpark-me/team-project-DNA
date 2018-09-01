const config = require('../utils/config');

/* redis */
const redis = require('redis').createClient(process.env.REDIS_PORT, process.env.EC2_HOST);
redis.auth(process.env.REDIS_PASSWORD);

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

//- Establish a new connection
connection.connect(function(err){
  if(err) {
      // mysqlErrorHandling(connection, err);
      console.log("\n\t *** Cannot establish a connection with the database. ***");

      connection = reconnect(connection);
  }else {
      console.log("\n\t *** New connection established with the database. ***")
  }
});

//- Reconnection function
function reconnect(connection){
  console.log("\n New connection tentative...");

  //- Destroy the current connection variable
  if(connection) connection.destroy();

  //- Create a new one
  var connection = mysql.createConnection(dbConfig);

  //- Try to reconnect
  connection.connect(function(err){
      if(err) {
          //- Try to connect every 2 seconds.
          setTimeout(reconnect, 2000);
      }else {
          console.log("\n\t *** New connection established with the database. ***")
          return connection;
      }
  });
}

//- Error listener
connection.on('error', function(err) {

  //- The server close the connection.
  if(err.code === "PROTOCOL_CONNECTION_LOST"){    
      console.log("/!\\ Cannot establish a connection with the database. /!\\ ("+err.code+")");
      connection = reconnect(connection);
  }

  //- Connection in closing
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_QUIT"){
      console.log("/!\\ Cannot establish a connection with the database. /!\\ ("+err.code+")");
      connection = reconnect(connection);
  }

  //- Fatal error : connection variable must be recreated
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_FATAL_ERROR"){
      console.log("/!\\ Cannot establish a connection with the database. /!\\ ("+err.code+")");
      connection = reconnect(connection);
  }

  //- Error because a connection is already being established
  else if(err.code === "PROTOCOL_ENQUEUE_HANDSHAKE_TWICE"){
      console.log("/!\\ Cannot establish a connection with the database. /!\\ ("+err.code+")");
  }

  //- Anything else
  else{
      console.log("/!\\ Cannot establish a connection with the database. /!\\ ("+err.code+")");
      connection = reconnect(connection);
  }
});

/* mongodb */
const mongoose = require('mongoose');

const url = `mongodb://${process.env.EC2_HOST}:${process.env.MONGO_PORT}/${process.env.DB_NAME}`;
const options = {
  user: process.env.MONGO_USERNAME,
  pass: process.env.MONGO_PASSWORD,
  useNewUrlParser: true,
  promiseLibrary: global.Promise
};
mongoose.connect(url, options);

const mongo = {};
mongo.db = mongoose.connection;
mongo.db.on('error', console.error);
mongo.db.once('open', function(){
  console.log("... Connected to mongod server");
  createSchema(config); // utils/config에 등록된 스키마 및 모델 객체 생성
});
mongo.db.on('disconnected', function(){
  console.log("... Disconnected to mongod server");
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
    console.log("... [%s], [%s] is added to mongo Object.",
                 curItem.schemaName, curItem.modelName);
  }
};

module.exports.mysql = connection;
module.exports.redis = redis;
module.exports.mongo = mongo;