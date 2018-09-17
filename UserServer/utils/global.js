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

connection.connect(function(err){
  if (err) {
      console.log("[MYSQL] Cannot establish a connection with the database MySQL ... ");
      connection = reconnect(connection);
  } else {
      console.log("[MYSQL] *** New connection established with the database MySQL ...")
  }
});

function reconnect(connection){
  console.log("[MYSQL] New connection tentative ...");

  if (connection) connection.destroy(); // 현재 커넥션이 존재한다면 끊고 새로 만든다.
  connection = mysql.createConnection(dbConfig);

  connection.connect(function(err){
      if (err) setTimeout(reconnect, 2000); // 2초마다 연결을 요청한다.
      else {
          console.log("[MYSQL] *** New connection established with the database ... ")
          return connection;
      }
  });
}

connection.on('disconnected', function(){
  console.log("[MySQL]] Connection disconnected with the database MySQL ...");
});

connection.on('error', function(err) {
  if(err.code === "PROTOCOL_CONNECTION_LOST"){ // 서버 측에서 연결을 끊은 경우
    console.log("[MYSQL] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_QUIT"){ // 커넥션이 강제로 끊긴 경우
    console.log("[MYSQL] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_AFTER_FATAL_ERROR"){ // Fatal error 발생
    console.log("[MYSQL] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
  else if(err.code === "PROTOCOL_ENQUEUE_HANDSHAKE_TWICE"){ // 이미 커넥션이 존재하는 경우
    console.log("[MYSQL] !!! Cannot establish a connection with the database : ("+err.code+")");
  }
  else{ // etc
    console.log("[MYSQL] !!! Cannot establish a connection with the database : ("+err.code+")");
    connection = reconnect(connection);
  }
});

setInterval(function () {
    connection.query('SELECT 1');
}, 5000);

module.exports.mysql = connection;
module.exports.redis = redis;