/******************************************************************************
' 파일     : redis_test.js
' 작성     : 박소영
' 목적     : 레디스에 테스트용 유저의 세션 정보를 저장합니다.
' 사용방식  : node redis_test.js @param inboundCount, @param outbound_count, @param Lng, @param Lat
'           node ./test/redis_test.js 20 80 127.101768 37.590039
******************************************************************************/

const args = process.argv.slice(2);
const async = require('async');
require('dotenv').config();
global.utils = require('./../utils/global');

if (args.length < 4) { // 인자가 2개보다 적을 경우에는 그냥 종료합니다.
  process.exit(1);
}

const inboundCount = parseInt(args[0]);
const outboundCount = parseInt(args[1]);
const currentLng = args[2];
const currentLat = args[3];
const storeFunction = require('./../utils/session').storeAll;
const helpers = require('./../utils/helpers');

const minInbountLng = (Math.floor(currentLng * 10)) * 0.1;
const minInbountLat = (Math.floor(currentLat * 10)) * 0.1;

async.waterfall([
  // 먼저 레디스를 초기화합니다.
  function init(next) {
    global.utils.redis.del("info");
    global.utils.redis.del("clients");;

    next(null);
  },
        
  function makeInBoundCount(next) {
     // 위치와 상관없는 outbountCount 수를 생성합니다.
    for (let i=0; i<outboundCount; i++) {
      let idx = helpers.randomNumber(100, 1000);
      let socket = helpers.randomString(20, 20);
      let position = helpers.randomGeoLocation();
      let radius = helpers.randomNumber(100, 2000);
      let nickname = helpers.randomString(4, 7);

      let testData = {
        idx,
        position,
        radius,
        nickname,
        avatar: null
      }
      console.log("Dummy data is added : " + i);
      storeFunction(socket, testData);    
    }   

    // 다음으로 현재 위치 내부에 있는 유저를 inboundCount 수 만큼 생성합니다.
    for (let i=0; i<inboundCount; i++) {
      let idx = helpers.randomNumber(100, 1000);
      let socket = helpers.randomString(20, 20);
      let position = helpers.randomGeoLocation(minInbountLng, minInbountLat);
      let radius = helpers.randomNumber(100, 2000);
      let nickname = helpers.randomString(4, 7);

      let testData = {
        idx,
        position,
        radius,
        nickname,
        avatar: null
      }

      console.log("Dummy data is added : " + (i+outboundCount));
      storeFunction(socket, testData);    
    }          
  }
]);
