const mongo = global.utils.mongo;
const paginationCount = require('../utils/config').pagination_count;
const helpers = require('../utils/helpers');

/*******************
 *  Save
 *  @param: dmData = {idx, roomIdx, contents, type}
 ********************/
exports.save = (dmData) => {
  // 1. roomIdx 값으로 room 값 찾아오기 (없으면 전송 불가)
  return new Promise((resolve, reject) => {
    mongo.roomModel.selectOne(parseInt(dmData.roomIdx), (err, room) => {
      if (err) {
        const customErr = new Error("Error occrred while selecting Room: " + err);
        reject(customErr);  
      } else {
        if (room.length > 0) {
          const receiver = (dmData.idx === room[0].users[0].idx) ? 
            room[0].users[1].idx : room[0].users[0].idx;
          resolve(receiver);
        } else { // 해당 idx의 room이 없다.
          const customErr = new Error("There is no chat room for that index.");
          reject(customErr);
        }        
      }
    });
  })
  .then((receiver) => {
    // 2. model 생성하기
    return new Promise((resolve, reject) => {
      const dm = new mongo.dmModel(
        {
          sender_idx: dmData.idx,
          contents: dmData.contents,
          type: dmData.type,
          created_at: helpers.getCurrentDate()
        }
      );
      // 3. 해당 room에 dm 추가하기 (저장하기)
      mongo.roomModel.saveDM(dmData.roomIdx, dm, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while Save Direct Message: " + err);
          reject(customErr);        
        } else {
          const next = {
            dm, receiver
          }
          resolve(next);
        }
      });
    });
  })
  .then((next) => {
    return new Promise((resolve, reject) => {
      // 4. 해당 채팅방의 updated_at 변경하기
      const data = {
        contents: dmData.contents,
        type: dmData.type
      };

      mongo.roomModel.updated(dmData.roomIdx, data, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while Update Room's updated_at: " + err);
          reject(customErr);        
        } else {
          next.dm.roomIdx = dmData.roomIdx;
          resolve(next);
        }
      });
    });
  })
}



/*******************
 *  SelectAll
 *  @param: userIdx, roomIdx, page
 ********************/
exports.selectAll = (userIdx, roomIdx, page) => {
  return new Promise((resolve, reject) => { 
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.roomModel.selectOne(roomIdx, (err, room) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting All Direct Messages: " + err);
          reject(customErr);        
        } else {
          let DMs = [];

          if (!page) { // 페이지 인자가 없음 : 페이지네이션이 되지 않은 경우
            DMs = room[0].messages
          } else {     // 페이지 인자가 있음 : 페이지네이션 적용
            let start = (paginationCount * (page - 1));
            if (start < 0) {
              start = 0;
            }            
            let end = paginationCount * (page);

            DMs = room[0].messages.reverse().slice(start, end);
          }         

          const avatar = (room[0].users[0].idx === userIdx) 
            ? room[0].users[1].avatar : room[0].users[0].avatar;

          const result = {
            avatar,
            DMs
          };

          resolve(result);    
        }
    });
  });
};