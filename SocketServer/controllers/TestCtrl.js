const validator = require('validator');

const messageModel = require('../models/MessageModel');
const dmModel = require('../models/DMModel');
const userModel = require('../models/UserModel');
const helpers = require('../utils/helpers');

let validationError = {
  name:'ValidationError',
  errors:{}
};


/*******************
 *  testsave (message)
 *  @param: messageIdx
 ********************/
exports.save = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const id = req.userData.id;
  const nickname = req.userData.nickname;
  const avatar = req.userData.avatar;
  const lng = req.body.lng || req.params.lng;
  const lat = req.body.lat || req.params.lat;
  const type = req.body.type || req.params.type || "Message";
  const contents = req.body.contents || req.params.contents;
  
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!lng || validator.isEmpty(lng)) {
    isValid = false;
    validationError.errors.lng = { message : "Longitude is required" };
  }

  if (!lat || validator.isEmpty(lat)) {
    isValid = false;
    validationError.errors.lat = { message : "Latitude is required" };
  }

  if (!contents || validator.isEmpty(contents)) {
    isValid = false;
    validationError.errors.contents = { message : "Contents is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  if (type == 'LoudSpeaker') { // 확성기일 경우 해당 유저의 잔여 point를 조회한다.
    let points = 0;

    try {
      points = await userModel.selectPoints(idx);      
    } catch (err) {
      // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
      return next(err);
    }

    if (points < 100) { // 포인트가 100보다 모자랄 경우엔 보낼 수 없다.
      return res.status(401).json({
        status: 401,
        message: "Not enough points",
        result: { points }        
      });
    }
  }

  // 2. DB에 저장하기
  let result = '';
  try {
    const messageData = {
      idx, id, nickname, avatar, lng, lat, type, contents
    };    

    result = await messageModel.save(messageData);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  } finally {
    if (type == 'LoudSpeaker') { // 확성기일 경우 포인트를 차감한다.
      try {
        await userModel.reducePoints(idx);      
      } catch (err) {
        // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
        return next(err);
      }
    }
  }

  // 3. 저장 성공
  const respond = {
    status: 201,
    message : "Create Message Successfully",
    result 
  };
  return res.status(201).json(respond);
}


/*******************
 *  testlike
 *  @param: messageIdx
 ********************/
exports.like = async (req, res, next) => {
  const userIdx = req.userData.idx;
  const messageIdx = req.body.idx || req.params.idx;

   /* 1. 유효성 체크하기 */
   let isValid = true;

   if (!messageIdx || validator.isEmpty(messageIdx)) {
     isValid = false;
     validationError.errors.messageIdx = { message : "Message idx is required" };
   }
 
   if (!isValid) return res.status(400).json(validationError);
   /* 유효성 체크 끝 */

  // 2. DB에 저장하기
  let result = '';
  try {
    result = await messageModel.like(userIdx, messageIdx);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  let message = '';
      
  if (result.type === "push") {
    message = "Like pushed Successfully";
  } else if (result.type === "pop") {
    message = "Like popped Successfully"
  }
  // 3. 저장 성공
  const respond = {
    status: 201,
    message,
    result
  };

  return res.status(201).json(respond);
}


/*******************
 *  testsave (dm)
 *  @param: roomIdx, type, contents
 ********************/
exports.savedm = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const roomIdx = req.body.room_idx || req.params.room_idx;
  const type = req.body.type || req.params.type;
  const contents = req.body.contents || req.params.contents;
  
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!roomIdx || validator.isEmpty(roomIdx)) {
    isValid = false;
    validationError.errors.roomIdx = { message : "Room index is required" };
  }

  if (!contents || validator.isEmpty(contents)) {
    isValid = false;
    validationError.errors.contents = { message : "Direct Message Contents is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. DB에 저장 요청하기
  let result = '';
  try {
    const dmData = {
      idx, roomIdx, contents
    };

    result = await dmModel.save(dmData);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 3. 저장 성공
  const respond = {
    status: 201,
    message : "Create Direct Message Successfully",
    result
  };
  return res.status(201).json(respond);
}