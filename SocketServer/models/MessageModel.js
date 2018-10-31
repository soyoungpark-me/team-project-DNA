const mongo = global.utils.mongo;
const testMongo = global.utils.testMongo;

const helpers = require('../utils/helpers');

/*******************
 *  Save
 *  @param: messageData = {idx, nickname, avatar, lat, lon, contents, testing}
 ********************/
exports.save = (messageData) => {
  let db = mongo;
  if (messageData.testing) { // 테스트 환경일 경우엔 DB을 테스트용으로 바꾼다.
    db = testMongo;
    console.log("... test data is saved");
  }

  // 1. model 생성하기
  return new Promise((resolve, reject) => {      
    const message = new db.messageModel(
      {
        user: {
          idx: messageData.idx,
          nickname: messageData.nickname,
          avatar: messageData.avatar
        },
        position: {
          type: "Point",
          coordinates: [messageData.lng, messageData.lat]
        },
        type: messageData.type,
        contents: messageData.contents,
        created_at: helpers.getCurrentDate()
      }
    );

    // 3. save로 저장
    message.save((err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(result);
      }
    });
  })
  // .then((idx) => {
  //   return new Promise((resolve, reject) => {   
  //     db.messageModel.selectOne(idx, (err, result) => {
  //       if (err) {
  //         const customErr = new Error("Error occrred while selecting All Messages: " + err);
  //         reject(customErr);        
  //       } else {
  //         resolve(result);
  //       }
  //     });
  //   });
  // });
};


/*******************
 *  SelectOne
 *  @param: idx
 ********************/
exports.selectOne = (idx) => {
  return new Promise((resolve, reject) => {      
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectOne(idx, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting All Messages: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};


/*******************
 *  SelectAll
 *  @param: blocks, page
 ********************/
exports.selectAll = (blocks, page) => {
  return new Promise((resolve, reject) => { 
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectAll(blocks, page, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting All Messages: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};


/*******************
 *  SelectCircle
 *  @param: conditions = {lng, lat, radius}, blocks, page
 ********************/
exports.selectCircle = (conditions, blocks, page) => {
  return new Promise((resolve, reject) => {      
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectCircle(conditions, blocks, page, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting Messages: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};



/*******************
 *  Like
 *  @param: userIdx, messageIdx
 ********************/
exports.like = (userIdx, messageIdx) => {
  return new Promise((resolve, reject) => {
    // 1. 먼저 내 idx가 좋아요 리스트에 있는지 확인
    mongo.messageModel.selectOne(messageIdx, (err, message) => {
      if (err) {
        const customErr = new Error("Error occrred Check likes list: " + err);
        reject(customErr);  
      } else {
        const result = message[0].likes.includes(userIdx);
        resolve(result);
      }      
    });
  })
  .then((exist) => {
    return new Promise((resolve, reject) => {
      if (!exist) { // 추가해야 한다.
        mongo.messageModel.like(userIdx, messageIdx, (err, result) => {
          if (err) {
            const customErr = new Error("Error occrred Push likes list: " + err);
            reject(customErr);  
          } else {
            resolve(result);    
          }
        });
      } else {        // 빼야 한다.
        mongo.messageModel.dislike(userIdx, messageIdx, (err, result) => {
          if (err) {
            const customErr = new Error("Error occrred Pop likes list: " + err);
            reject(customErr);  
          } else {
            resolve(result);    
          }
        });
      }
    });
  });
};



/*******************
 *  Dislike
 *  @param: userIdx
 ********************/
exports.dislike = (userIdx, roomIdx) => {
  return new Promise((resolve, reject) => {
    // 1. 먼저 내 idx가 좋아요 리스트에 있는지 확인
    mongo.messageModel.selectOne(messageIdx, (err, message) => {
      if (err) {
        const customErr = new Error("Error occrred Check likes list: " + err);
        reject(customErr);  
      } else {
        const result = message[0].likes.includes(userIdx);
        
        if (!result) { // 존재해야 한다!
          const customErr = new Error("ID is not added to the list: " + err);
          reject(customErr);  
        } else {
          resolve();
        }
      }      
    });
  })
  .then(() => {
    return new Promise((resolve, reject) => {
      mongo.messageModel.dislike(userIdx, messageIdx, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred cancle likes list: " + err);
          reject(customErr);  
        } else {
          resolve(result);    
        }
      });
    });
  });
}



/*******************
 *  Best
 *  @param: conditions = {lng, lat, radius}
 ********************/
exports.best = (conditions) => {
  return new Promise((resolve, reject) => {      
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectBest(conditions, (err, result) => {
        if (err) {
          const customErr = new Error("Error occrred while selecting Best Messages: " + err);
          reject(customErr);        
        } else {
          resolve(result);
        }
    });
  });
};