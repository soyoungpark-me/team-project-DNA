const express = require('express');
const http = require('http');
const https = require('https');
const fs = require('fs');

const path = require('path');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

require('dotenv').config();
global.utils = require('./utils/global');
require('./routes')(app);

/* Error Handler*/
process.stdin.resume(); //so the program will not close instantly

function exitHandler(options, exitCode) {
  if (options.cleanup || exitCode || exitCode === 0 || options.exit) {
    global.utils.redis.del("clients");
    global.utils.redis.del("geo:locations");
    global.utils.redis.del("info");
  }  

  if (options.exit) {
    process.exit();
  }
}

process.on('exit', exitHandler.bind(null,{cleanup:true}));            // do something when app is closing
process.on('SIGINT', exitHandler.bind(null, {exit:true}));            // catches ctrl+c event
process.on('SIGUSR1', exitHandler.bind(null, {exit:true}));           // catches "kill pid"
process.on('SIGUSR2', exitHandler.bind(null, {exit:true}));
// process.on('uncaughtException', exitHandler.bind(null, {exit:true})); // uncaught exceptions

// const server = require('http')
//   .createServer(lex.middleware(require('redirect-https')()))

let server, corsOptions;
switch(process.env.NODE_ENV){
  case 'development':    
    corsOptions = {
      origin: 'http://localhost:9010',
      credentials : true
    };
    app.use(cors(corsOptions));
    server = http.Server(app);    
    break;

  case 'production':
    corsOptions = {
      origin: 'https://dna.soyoungpark.me',
      credentials : true
    };
    app.use(cors(corsOptions));
    // Certificate
    try {
      const privateKey = fs.readFileSync('../SSL/privkey.pem', 'utf8');
      const certificate = fs.readFileSync('../SSL/cert.pem', 'utf8');
      const ca = fs.readFileSync('../SSL/chain.pem', 'utf8');

      const credentials = {
        key: privateKey,
        cert: certificate,
        ca: ca
      };
      
      server = https.Server(credentials, app);
    } catch (error) {
      console.log(error);
    }
    break;

  default:
    return;
}

const socket = require('./utils/socket').init(server);
server.listen(process.env.PORT, process.env.HOST, () => {
  console.info('[DNA-SocketApiServer] Listening on port %s at %s', 
  process.env.PORT, process.env.HOST);
});

module.exports = app;

// app.get('/message', function(req, res){
//   res.sendFile(__dirname + '/test_message.html');
// });

// app.get('/dm', function(req, res){
//   res.sendFile(__dirname + '/test_dm.html');
// });