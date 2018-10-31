/******************************************************************************
' 파일     : session.js
' 작성     : 박소영
' 목적     : 레디스에 저장되는 세션과 관련된 함수들을 모아논 파일입니다.
******************************************************************************/

const redis = global.utils.redis;
const pub = global.utils.pub;
const geolib = require('geolib');
const helpers = require('./helpers');
/* 
  레디스에 전달 받은 유저 정보를 저장하는 함수입니다.
  @param id       : 저장할 유저의 idx
  @param data     : 저장할 내용을 담은 JSON 오브젝트
         socket   : 해당 유저가 현재 물고 있는 소켓의 아이디
         position : 유저의 현재 위치
         radius   : 유저가 설정한 반경 값
         nickname : 유저의 닉네임
         avatar   : 유저의 프로필 이미지 주소
*/
exports.storeAll = (socketId, data) => {
  const idx = data.idx;
  const position = data.position;  
  
  const info = {
    socket: socketId,
    position: position,
    radius: data.radius,
    nickname: data.nickname,
    avatar: data.avatar,
  };

  if(idx && idx !== undefined){
    storeHashMap("client", socketId, idx);
    storeHashMap("info", idx, JSON.stringify(info));
    
    redis.geoadd("geo", position[0], position[1], idx);
  }      
}

const storeHashMap = (type, key, value) => {
  redis.hmset(type, key, value);
  // TYPE       key         value
  // client     socket ID   user idx
  // info       user idx    user info JSON
};

// 정보가 레디스에 존재하는지 체크하지 않아도 자동으로 갱신됩니다.
// This command overwrites any specified fields already existing in the hash.
// If key does not exist, a new key holding a hash is created.      


/* 
  해당 메시지의 좌표 값을 통해 해당 메시지를 받아볼 유저들을 추려내는 함수입니다.
  @param io       : pub/sub을 위한 io 객체
  @param socket   : 연결한 소켓 자체
  @param response : 메시지의 내용
  @param event    : 클라이언트로 emit할 이벤트 이름
*/
exports.findUserInBound = (io, socket, response, event) => {
  return new Promise(async (resolve, reject) => {
    // 먼저 현재 위치를 기반으로 client 리스트를 뽑아옵니다.
    const clientList = await this.returnSessionList("client");
    
    if (clientList) {
      const next = {
        clientList, response
      };
      resolve(next);
    } else {
      reject();
    }
  })
  .then((next) => {
    // 2. 다음으로 해당 client 리스트 별로 info 정보를 가져옵니다.
    return new Promise((resolve, reject) => {
      Object.keys(next.clientList).forEach(function (key) {
        const idx = next.clientList[key];

        redis.hmget("info", idx, (err, result) => {
          if (err) {
            console.log(err);
            reject();
          }
  
          if (result.length > 0) {
            const value = JSON.parse(result[0]);
            const messageLng = next.response.result.position.coordinates[0];
            const messageLat = next.response.result.position.coordinates[1];

            if (value && value !== null) {
              const distance = geolib.getDistance(
                { latitude: value.position[1], longitude: value.position[0] }, // 소켓의 현재 위치 (순서 주의!)
                { latitude: messageLat, longitude: messageLng }                // 메시지 발생 위치
              );
              if (value.radius >= distance) { 
                // 거리 값이 설정한 반경보다 작을 경우에만 이벤트를 보내줍니다.    
                // 보내주기 전에, 해당 socket이 현재 이 서버에 존재하는지 확인합니다.
                // 없다면 redis의 pub/sub을 이용해 다른 서버에 뿌려줘야 합니다.
                if (Object.keys(io.sockets.sockets).includes(key)){ // 존재할 경우 직접 보냅니다.
                  socket.broadcast.to(key).emit(event, next.response) 
                } else {
                  const data = {
                    socketId: key,
                    event,
                    response: next.response
                  };
                  pub.publish('socket', JSON.stringify(data));
                }
              }
            }
          }
        });
      });
      socket.emit(event, response); // 자신에게도 전송합니다.
    })
  });
};

/* 
  redis에 저장되어 있는 세션 값들을 반환합니다.
  @param type     : client, info, geo 중 하나
  @param position : 유저를 찾을 기준이 되는 좌표 값
  @param radius   : (type이 geo일 경우) 반경 값
*/
exports.returnSessionList = (type, position, radius) => {
  return new Promise((resolve, reject) => {    
    if (type === "geo") {   // 타입이 geo일 경우엔 GEO API를 사용합니다.
      redis.georadius(type, position[0], position[1], radius, "m", (err, positions) => {
        if (err) {
          console.log(err);
          reject(err);
        }
        // resultForGeo = resultForGeo.concat(positions);
        // resolve(resultForGeo);        
        resolve(positions);  
      });
    } else {                // 타입이 client이거나 info일 경우엔 hash 사용      
      redis.hgetall(type, (err, object) => {
        if (err) {
          console.log(err);
          reject(err);
        }
        resolve(object);
      });
    }
  });
}

/* 
  소켓ID를 주면 해당 소켓에 물려있는 유저의 타일 키 값을 반환합니다.
  @param socketID : 찾고 싶은 유저의 소켓 ID
*/
exports.returnMapKey = (socketID) => {
  return new Promise((resolve, reject) => {
    redis.hmget("tilemap", socketID, (err, result) => {
      if (err) {
        console.log(err);
        reject();
      } else {        
        if (result && result.length > 0) {
          resolve(result[0]);
        } else {
          resolve(null);
        }
      }
    });
  });
}

/* 
  소켓ID를 주면 해당 소켓에 물려있는 유저의 정보를 레디스에서 모두 삭제합니다.
  @param socketID : 삭제할 유저의 소켓 ID
*/
exports.removeSession = async (socketID) => {
  redis.hmget("client", socketID, (err, result) => {
    if (err) {
      console.log(err);                
    } else {
      if (result && result.length > 0) {
        const idx = result[0];
        if (idx && idx !== null) {
          redis.zrem("geo", idx);
          redis.hdel("info", idx);
        }
      }
      redis.hdel("client", socketID);
    }
  });
}