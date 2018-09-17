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

require('dotenv').config();
global.utils = require('./utils/global');
require('./routes')(app);

// 로컬에서 개발하는 development 모드와, 실제로 배포하는 production 모드로 나누어,
// 크로스 도메인 문제를 해결하고 production 모드의 경우 https를 적용합니다.
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

const args = process.argv.slice(2);
if (args.length < 1) {
  process.exit();
}

require('./utils/socket').init(server);
server.listen(args[0], process.env.HOST, () => {
  console.info('[DNA-SocketApiServer] Listening on port %s at %s', 
  args[0], process.env.HOST);
});

module.exports = app;