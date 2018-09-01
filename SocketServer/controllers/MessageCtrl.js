const validator = require('validator');

const messageModel = require('../models/MessageModel');
const userModel = require('../models/UserModel');
const authModel = require('../models/AuthModel');
const helpers = require('../utils/helpers');

let validationError = {
  name:'ValidationError',
  errors:{}
};


/*******************
 *  Save
 *  param: lng, lat, type, contents
 *  TODO 에러 코드 정리 및 PUSH
 ********************/
exports.save = (token, param) => {
  // 1. 먼저 해당 jwt가 유효한지 확인 
  return new Promise((resolve, reject) => {
    authModel.auth(token, (err, userData) => {
      if (err) {
        reject(err);
      } else {
        resolve(userData);
      }
    });
  })
  .then((userData) => {
    return new Promise(async (resolve, reject) => {
      /* PARAM */
      const idx = userData.idx;
      const id = userData.id;
      const nickname = userData.nickname;
      const avatar = userData.avatar || null;
      const lng = param.lng;
      const lat = param.lat;
      const contents = param.contents;
      const type = param.type || "Message";
      
      /* 2. 유효성 체크하기 */
      let isValid = true;

      if (!lng || lng === undefined || lng === '') {
        isValid = false;
        validationError.errors.lng = { message : "Longitude is required" };
      }

      if (!lat || lat === undefined || lat === '') {
        isValid = false;
        validationError.errors.lat = { message : "Latitude is required" };
      }

      if (!contents || contents === null || contents === undefined) {
        isValid = false;
        validationError.errors.contents = { message : "Contents is required" };
      }      

      if (!isValid) reject();
      /* 유효성 체크 끝 */

      let response = '';

      if (type === "LoudSpeaker") { // 확성기일 경우 해당 유저의 잔여 point를 조회한다.
        let points = 0;

        try {
          points = await userModel.selectPoints(idx);      
        } catch (err) {
          // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
          reject(err);
        }

        if (points < 100) { // 포인트가 100보다 모자랄 경우엔 보낼 수 없다.
          response = {
            status: 401,
            message: "Not enough points",
            result: { points }        
          };
          reject();
        }
      } else if (type === "Image") {
        console.log(contents);

        // const response = helpers.uploadSingle(contents);
        // console.log(response);
        // contents = "이미지 도전중";

        // 이미지일 경우에는 DB에 파일의 경로만 저장해야 합니다.
      }

      // 3. DB에 저장하기
      const messageData = {
        idx, id, nickname, avatar, lng, lat, type, contents
      };    
      
      try {
        result = await messageModel.save(messageData); 
      } catch (err) {
        // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
        reject(err);
      } finally {
        if (type == 'LoudSpeaker') { // 확성기일 경우 포인트를 차감한다.
          try {
            await userModel.reducePoints(idx);      
          } catch (err) {
            // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
            reject(err);
          }
        }
      }
      response = {
        status: 201,
        message: "Save Message Successfully",
        result: result[0]
      };
      // 4 등록 성공! 소켓으로 다시 반대로 쏴줘야 한다. 
      resolve(response);
    });
  });
};



/*******************
 *  selectOne
 *  param : idx
 ********************/
exports.selectOne = async (req, res, next) => {
  /* PARAM */
  const idx = req.body.idx || req.params.idx;

  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!idx || validator.isEmpty(idx)) {
    isValid = false;
    validationError.errors.idx = { message : "idx is required" };
  }

  if (!isValid) {
    reject();
  }
  /* 유효성 체크 끝 */

  // 2. DB에서 끌고 오기
  let result = '';
  try {
    result = await messageModel.selectOne(idx);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 3. 조회 성공
  const respond = {
    status: 200,
    message : "Select Messages Successfully",
    result: result
  };
  return res.status(200).json(respond);
}



/*******************
 *  selectAll
 *  @param: page
 ********************/
exports.selectAll = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const page = req.body.page || req.params.page;
  
  // 1. 차단 리스트 끌고 오기
  let blocks = '';
  try {
    blocks = await userModel.selectBlock(idx);
  } catch (err) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(err);
  }

  // 2. DB에서 끌고 오기
  let result = '';
  try {
    result = await messageModel.selectAll(blocks, page);
  } catch (err) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(err);
  }

  // 2. 조회 성공
  const respond = {
    status: 200,
    message : "Select Messages Successfully",
    result: result
  };
  return res.status(200).json(respond);
}


/*******************
 *  selectCircle
 *  @param: lng, lat, radius, page
 ********************/
exports.selectCircle = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const lng = req.body.lng || req.params.lng;
  const lat = req.body.lat || req.params.lat;
  const radius = req.body.radius || req.params.radius;
  const page = req.body.page || req.params.page;  
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!lng || lng === '' || lng === undefined) {
    isValid = false;
    validationError.errors.lng = { message : "Longitude is required" };
  }

  if (!lat || lat === '' || lat === undefined) {
    isValid = false;
    validationError.errors.lat = { message : "Latitude is required" };
  }

  if (!radius || radius === '' || radius === undefined) {
    isValid = false;
    validationError.errors.radius = { message : "Radius is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. 차단 리스트 끌고 오기
  let blocks = '';
  try {
    blocks = await userModel.selectBlock(idx);
  } catch (err) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(err);
  }

  // 3. DB에서 끌고 오기
  let result = '';
  try {
    const conditions = {
      lng, lat, radius
    };

    result = await messageModel.selectCircle(conditions, blocks, page);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 4. 조회 성공
  const respond = {
    status: 200,
    message : "Select Messages Successfully",
    result
  };
  return res.status(200).json(respond);
};



/*******************
 *  like
 *  @param: messageIdx
 ********************/
exports.like = (token, messageIdx) => {
  // 1. 먼저 해당 jwt가 유효한지 확인 
  return new Promise((resolve, reject) => {
    authModel.auth(token, (err, userData) => {
      if (err) {
        reject(err);
      } else {
        resolve(userData);
      }
    });
  })
  .then((userData) => {
    return new Promise(async (resolve, reject) => {
      const userIdx = userData.idx;

      /* 1. 유효성 체크하기 */
      let isValid = true;

      if (messageIdx === null || messageIdx === undefined) {
        isValid = false;
        validationError.errors.messageIdx = { message : "Message idx is required" };
      }
    
      if (!isValid) reject();
      /* 유효성 체크 끝 */

      // 2. DB에 저장하기
      let result = '';
      try {
        result = await messageModel.like(userIdx, messageIdx);
      } catch (err) {
        // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
        console.log(err);
        reject(err);      
      }

      // 3. 저장 성공
      const respond = {
        status: 201,
        result
      };

      resolve(respond);
    });
  });
};



/*******************
 *  Best
 *  @param: lng, lat, radius
 ********************/
exports.best = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const lng = req.body.lng || req.params.lng;
  const lat = req.body.lat || req.params.lat;
  const radius = req.body.radius || req.params.radius;
  
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!lng || lng === '' || lng === undefined) {
    isValid = false;
    validationError.errors.lng = { message : "Longitude is required" };
  }

  if (!lat || lat === '' || lat === undefined) {
    isValid = false;
    validationError.errors.lat = { message : "Latitude is required" };
  }

  if (!radius || radius === '' || radius === undefined) {
    isValid = false;
    validationError.errors.radius = { message : "Radius is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. DB에서 끌고 오기
  let result = '';
  try {
    const conditions = {
      lng, lat, radius
    };

    result = await messageModel.best(conditions);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 3. 조회 성공
  const respond = {
    status: 200,
    message : "Select Messages Successfully",
    result
  };
  return res.status(200).json(respond);
};