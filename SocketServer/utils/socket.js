/******************************************************************************
' 파일     : socket.js
' 작성     : 박소영
' 목적     : Socket IO 이벤트들을 정리해놓은 파일입니다.
******************************************************************************/

const redis = global.utils.redis;
const pub = global.utils.pub;
const sub = global.utils.sub;
const rabbitMQ = global.utils.rabbitMQ;
const logger = global.utils.logger;

const geolib = require('geolib');
const fetch = require('node-fetch');
const messageCtrl = require('../controllers/MessageCtrl');
const dmCtrl = require('../controllers/DMCtrl');
const helpers = require('./helpers');
const errorCode = require('./error').code;
const config = require('./config');
const session = require('./session');

exports.init = (http) => {
  const io = require('socket.io')(http, 
    {'pingInterval': config.ping_interval, 'pingTimeout': config.ping_timeout});
  
  // 서버간 pub/sub을 위해 socket 구독
  sub.subscribe('socket');

  // data = { socketId, event, response }
  sub.on('message', (channel, data) => {
    const parsed = JSON.parse(data);

    if (channel === 'socket') {
      // 여기에 들어왔다는 것은, 메시지가 도착했는데 소켓이 그 서버에는 없었다는 뜻입니다.
      // 동시에 다른 서버에도 pub을 했을 테니까... 
      // 여기서도 똑같이 있으면 처리하고, 대신 없으면 다시 pub 해줄 필요없이 무시합니다.
      if (Object.keys(io.sockets.sockets).includes(parsed.socketId)) {
        io.sockets.to(parsed.socketId).emit(parsed.event, parsed.response);
      }      
    }
  });

  io.on('connection', (socket) => {
    /*******************
     * 소켓 에러 로그
    ********************/
    socket.on('error', (error) => {
      logger.log("error", "Error: websocket error", error);
    }).on('connect_error', (error) => {
      logger.log("error", "Error: websocket error", error);
    }).on('reconnect_error', (error) => {
      logger.log("error", "Error: websocket error", error);
    });

    /*******************
     * 소켓 연결
    ********************/
    // 클라에서 보내온 정보를 레디스에 저장합니다.
    socket.on('store', (data) => {
      if (Object.prototype.toString.call(data) == "[object String]") {
        data = JSON.parse(data);
      }
      session.storeAll(socket.id, data);
    });

    // 클라의 연결이 종료되었을 경우 레디스에서 해당 정보를 삭제합니다.
    socket.on('disconnect', () => {
      session.removeSession(socket.id);
    });

    // 클라가 주기적으로 현재 위치를 업데이트하면 이를 레디스에서 갱신합니다.
    socket.on('update', async (type, data) => {    
      if (Object.prototype.toString.call(data) == "[object String]") {
        data = JSON.parse(data);
      }
      session.storeAll(socket.id, data);
      const position = data.position;

      // 해당 위치와 radius에 맞는 접속자와 접속중인 친구들을 찾아 보내줍니다.
      // 유저에게 type을 받아서, 이에 맞는 정보를 찾아서 보내주면 됩니다.
      //    geo : 현재 위치와 반경 내에 존재하는 접속자 리스트 return
      //    dm  : 접속 중인 친구 리스트 return

      if (type === "geo") {
        new Promise(async (resolve, reject) => {
          // nearby @param : {위도, 경도}, 반경
          // 현재 유저의 위치로부터 유저가 설정한 반경값 이내에 존재하는 접속자만 추려냅니다.
          // + 기능 추가 : 주변에 접속중인 사람인지만 보여주지 말고, 그 유저도 내 메시지를 받아볼 수 있는지도 추가합니다.
          const positions = await session.returnSessionList("geo", position, data.radius);
          positions.length > 0 ? resolve(positions) : reject();
        })
        .then((positions) => {
          return new Promise((resolve, reject) => {
            let infoList = [];

            positions.map(async (idx, i) => {
              redis.hmget("info", idx, (err, info) => {
                if (err) {
                  logger.log("error", "Error: websocket error", err);
                  console.log(err);
                  reject(err);
                }
                else {
                  const json = JSON.parse(info[0]);
                  if (json !== null) {
                    // 일단 상대가 내 반경 안에 들어와 있다면, 나도 상대의 반경 안에 들어가 있는지도 체크합니다.
                    const distance = geolib.getDistance(
                      { latitude: position[1], longitude: position[0] }, // 내 위치 (순서 주의!)
                      { latitude: json.position[1], longitude: json.position[0] }    // 상대의 위치
                    );  

                    let inside = false;  // 거리가 해당 유저의 반경보다 작은 경우는 참으로 바꿉니다.
                    if (distance <= json.radius) inside = true;
                    
                    const result = {
                      idx,
                      nickname: json.nickname,
                      avatar: json.avatar,
                      inside
                    };
                    
                    infoList.push(result);

                    if (i+1 === positions.length) {
                      socket.emit("geo", infoList);
                    }
                  }
                }
              });           
            });
          });
        });     
      } else if (type === "direct"){
        // 친구 리스트를 받아와서 접속 중인 사람 중에서 추려서 돌려주면 됩니다.)
        let infoList = [];
        infoList.push({
          idx: data.idx
        });

        fetch(process.env.WAS_SERVER + "/friends/show", {
          method: "GET",
          headers: {"token": data.token, 'Content-Type': 'application/json' },
          withCredentials: true,
          mode: 'no-cors'
        })
        .then(res => res.json())
        .then((response) => {
          if (response && (response.status === 201 || response.status === 200)) {
            response.result.map((row, i) => {
              const friendIdx = (row.user1_idx === data.idx ? row.user2_idx : row.user1_idx);
              redis.hmget("info", friendIdx, (err, info) => {
                if (err) console.log(err);
                if (info && info.length > 0) {
                  const json = JSON.parse(info[0]);

                  if (json) {
                    const result = {
                      idx: friendIdx,
                      nickname: json.nickname,
                      avatar: json.avatar,
                      inside: true
                    };

                    infoList.push(result);                    
                  }
                }

                if (i+1 === response.result.length) {
                  socket.emit("direct", infoList);
                }
              })
            })
          }
        })
        .catch((err) => {
          console.log(err);
        });
      }
    });

    

    /*******************
     * 메시지 생성
    ********************/

    // 새로 메시지를 생성했을 경우에는
    socket.on('save_msg', async (data) => {
      if (Object.prototype.toString.call(data) == "[object String]") {
        data = JSON.parse(data);
      }

      // 1. DB에 저장하기 위해 컨트롤러를 호출합니다.
      let response = '';  
      const token = data.token;
      const messageData = data.messageData;
            messageData.testing = data.testing;
      const radius = data.radius;
      
      try {
        response = await messageCtrl.save(token, messageData);
      } catch (err) {
        logger.log("error", "Error: websocket error", err);
        response = errorCode[err];
        console.log(err);
      } finally {
        if (!response || response === null) {        
          return;
        }
        
        const position = response.result.position.coordinates;
        session.findUserInBound(io, socket, response, "new_msg") ;

        // 4. 해당 메시지가 확성기 타입일 경우에는 푸시 메시지도 보내줘야 합니다.
        if (messageData.type === "LoudSpeaker") {
          rabbitMQ.channel.publish("push", "speaker", new Buffer(JSON.stringify(response)));
          // 5. 푸시 메시지를 보내줄 대상을 선별해줘야 합니다.        
          await new Promise(async (resolve, reject) => {
            // nearby @param : {위도, 경도}, 반경
            // 작성된 메시지로의 좌표값으로부터 주어진 반경 이내에 위치한 사용자만 추려냅니다.
            const positions = await session.returnSessionList("geo", position, radius);
            positions.length > 0 ? resolve(positions) : reject();
          })
          .then((positions) => {
            positions.map(async (target, i) => {
              redis.hgetall("client", (err, object) => {
                if (err) {
                  console.log(err);
                  return;
                } else {
                  const keys = Object.keys(object);
                  keys.forEach((key) => {
                    redis.hmget("client", key, (err, idx) => {
                      if (idx && idx[0] && idx[0] == target) {                  
                        const socketId = key;
                        
                        if (Object.keys(io.sockets.sockets).includes(socketId)){ // 존재할 경우 직접 보냅니다.
                          socket.to(socketId).emit('speaker', response.result);
                        } else {
                          const data = {
                            socketId,
                            event: "speaker",
                            response: response.result
                          };
                          pub.publish('socket', JSON.stringify(data));
                        }
                      }
                    });
                  });
                }
              });   
            });
          });
        }
      }
    });

    /*******************
     * 좋아요 처리
    ********************/

    socket.on('like', async (token, idx) => {
      // 1. DB에 저장하기 위해 컨트롤러를 호출한다.
      let response = '';

      try {
        response = await messageCtrl.like(token, idx);
      } catch (err) {
        logger.log("error", "Error: websocket error", err);
        response = errorCode[err];
        console.log(err);
      } finally {
        // 3. 결과물을 이 메시지를 받아보는 유저와 나에게 쏴야 합니다.
        // 기존 메시지 수신 방식이랑 동일하게 하면 됩니다.
        if (!response || response === null) {                  
          console.log(err);
          return;
        }
        session.findUserInBound(io, socket, response, "apply_like");
      }      
    });


    /*******************
     * DM 생성
    ********************/

    socket.on('save_dm', async (token, messageData) => {
      if (Object.prototype.toString.call(messageData) == "[object String]") {
        messageData = JSON.parse(messageData);
      }

      // 1. DB에 저장하기 위해 컨트롤러를 호출한다.
      let response = '';

      try {
        response = await dmCtrl.save(token, messageData);
      } catch (err) {
        logger.log("error", "Error: websocket error", err);
        response = errorCode[err];
        console.log(err);
      } finally {
        // 3. 결과물을 해당 유저와 나에게 쏴야 합니다.
        // redis의 세션 목록에 해당 유저가 있는지 확인하고, 있으면 쏩니다.
        const receiver = response.result.receiver;
        const sender = response.result.dm.sender_idx;
        
        redis.hgetall("client", (err, object) => {
          if (err) {
            console.log(err);
            return;
          } else {
            const keys = Object.keys(object);
            keys.forEach((key) => {
              redis.hmget("client", key, (err, idx) => {
                if (idx && idx[0] && (idx[0] == receiver || idx[0] == sender)) {                  
                  const socketId = key;

                  if (Object.keys(io.sockets.sockets).includes(socketId)){ // 존재할 경우 직접 보냅니다.
                    socket.broadcast.to(socketId).emit('new_dm', response);
                  } else {
                    const data = {
                      socketId,
                      event: "new_dm",
                      response
                    };
                    pub.publish('socket', JSON.stringify(data));
                  }
                }
              });
            });
          }
        });
        socket.emit('new_dm', response);       
      }      
    });
  });
};