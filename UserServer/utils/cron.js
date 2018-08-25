const redis = global.utils.redis;
const mysql = global.utils.mysql;

const userModel = require('../models/UserModel');
const helpers = require('../utils/helpers');
const config = require('../utils/config');

/* cron 설정 (redis 삭제) */
const cron = require("node-cron");
exports.setCron = () => {
  cron.schedule(config.cron_for_refresh, function() {
    console.log("---------------------");
    console.log("Running Cron Job : DELETE EXPIRED TOKEN");

    let count = 0;

    redis.hgetall('refreshTokens', function(err, reply) {
      if (reply) {
        Object.keys(reply).forEach(key => {
          // 날짜를 서로 비교해 현재보다 앞에 있을 경우 삭제한다.
          if (reply[key] && JSON.parse(reply[key]).expiresIn < new Date().getTime()) {
              redis.hdel('refreshTokens', key);
              count++;
          }
        });
      }
      console.log(count + " rows) Expired tokens deleted successfully");
    });
  });

  cron.schedule(config.cron_for_point, async function() {
    console.log("---------------------");
    console.log("Running Cron Job : GIVE ADDITIONAL POINTS");

    let count = 0;

    try {
      count = await userModel.addPoints();
    } catch (err) {
      console.log("Error occurred whild adding points: " + err);
    }
    console.log(count + " rows) is gived additional points successfully");
    
  });
}