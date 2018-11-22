const mongo = global.utils.mongo;
const testMongo = global.utils.testMongo;

const fetch = require('node-fetch');
const helpers = require('../utils/helpers');

/*******************
 *  Save
 *  @param: messageData = {idx, lat, lon, contents, testing}
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
          idx: messageData.idx
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
 *  @param: conditions = {token, lng, lat, radius}, blocks, page
 ********************/
exports.selectCircle = (conditions, blocks, page) => {
  return new Promise((resolve, reject) => {      
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectCircle(conditions, blocks, page, (err, result) => {
      let finalArray = [];
      if (err) {
        const customErr = new Error("Error occrred while selecting Messages: " + err);
        reject(customErr);        
      } else {
        result.forEach(element => {
          const idx = element.user.idx;
          fetch(process.env.USER_SERVER + "/user/" + idx, {
            method: "GET",
            headers: {"token": conditions.token, 'Content-Type': 'application/json' },
            withCredentials: true,
            mode: 'no-cors'
          })
          .then(res => res.json())
          .then((response) => {
            const finalData = {
              idx: element.idx,
              user: {
                idx,
                nickname: response.result.nickname,
                avatar: response.result.avatar,
                anonymity: response.result.anonymity
              },
              position: element.position,
              type: element.type,
              like_count: element.like_count,
              likes: element.likes,
              contents: element.contents,
              created_at: element.created_at
            };
            finalArray.push(finalData);
            
            if (finalArray.length === result.length) {
              resolve(finalArray);
            }
          });
        });
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
 *  @param: conditions = {token, lng, lat, radius}
 ********************/
exports.best = (conditions) => {
  return new Promise((resolve, reject) => {      
    // DB의 모델에서 바로 끌고 오면 된다.
    mongo.messageModel.selectBest(conditions, (err, result) => {      
      let finalArray = [];        
      if (err) {
        const customErr = new Error("Error occrred while selecting Best Messages: " + err);
        reject(customErr);        
      } else {
        result.forEach(element => {
          const idx = element.user.idx;
          fetch(process.env.USER_SERVER + "/user/" + idx, {
            method: "GET",
            headers: {"token": conditions.token, 'Content-Type': 'application/json' },
            withCredentials: true,
            mode: 'no-cors'
          })
          .then(res => res.json())
          .then((response) => {
            const finalData = {
              idx: element.idx,
              user: {
                idx,
                nickname: response.result.nickname,
                avatar: response.result.avatar,
                anonymity: response.result.anonymity
              },
              position: element.position,
              type: element.type,
              like_count: element.like_count,
              likes: element.likes,
              contents: element.contents,
              created_at: element.created_at
            };
            finalArray.push(finalData);
            
            if (finalArray.length === result.length) {
              resolve(finalArray);
            }
          });
        });
      }        
    });
  });
};