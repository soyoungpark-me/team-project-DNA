const mongo = global.utils.mongo;
const helpers = require('../utils/helpers');

/*******************
 *  Open
 *  @param: roomData = {
 *            user1 = { idx, nickname, avatar },
 *            user2 = { idx, nickname, avatar }
 *          }
 ********************/
exports.open = (roomData) => {  
  // 1. 해당 유저들의 채팅방이 존재하는지 확인
  return new Promise((resolve, reject) => {
    mongo.roomModel.search(roomData.user1.idx, roomData.user2.idx, (err, result) => {
      if (err) {
        const customErr = new Error("Error occrred while finding Room: " + err);
        reject(customErr);        
      } else {
        if (result.length === 0) { // 채팅방이 존재하지 않는다!
          resolve(null);
        } else {
          const customErr = new Error("The room already exists: " + err);
          reject(customErr);    
        }        
      }
    });
  })
  .then(() => {
    // 2. idx 최대값 구하기 
    return new Promise((resolve, reject) => {  
      mongo.roomModel.count((err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while Counting Rooms: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
      });
    })
    .then((count) => { 
      // 3. model 생성하기
      return new Promise((resolve, reject) => {  
        let idx = 0;
        
        if (count[0]) {
          idx = count[0].idx + 1;
        }

        const room = new mongo.roomModel(
          {
            idx,
            users: [{
              idx: roomData.user1.idx,
              nickname: roomData.user1.nickname,
              avatar: roomData.user1.avatar
            },{
              idx: roomData.user2.idx,              
              nickname: roomData.user2.nickname,
              avatar: roomData.user2.avatar
            }],
            created_at: helpers.getCurrentDate(),
            updated_at: helpers.getCurrentDate()
          }
        );

        // 3. save로 저장
        room.save((err) => {
          if (err) {
            reject(err);
          } else {
            resolve(room);
          }
        });
      });
    });
  });
}


/*******************
 *  SelectAll
 *  @param: userIdx, page
 ********************/
exports.selectAll = (userIdx, page) => {
  return new Promise((resolve, reject) => { 
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.roomModel.selectAll(userIdx, page, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting All Rooms: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};


/*******************
 *  Close
 *  @param: userIdx, roomIdx
 ********************/
exports.close = (userIdx, roomIdx) => {
  return new Promise((resolve, reject) => { 
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.roomModel.close(userIdx, roomIdx, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while Removing Room's DMs: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};



/*******************
 *  CheckUser
 *  @param: userIdx, roomIdx
 ********************/
exports.checkUser = (userIdx, roomIdx) => {
  return new Promise((resolve, reject) => { 
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.roomModel.selectOne(roomIdx, (err, room) => {
        if (err) {
          const customErr = new Error("Error occrred while Checking User is in Room : " + err);
          reject(customErr);        
        } else {
          if (room[0] && 
            (room[0].users[0].idx === userIdx || room[0].users[1].idx === userIdx)) { // 존재 확인!
            resolve(true);
          } else {
            resolve(false)
          }          
        }
    });
  });
};