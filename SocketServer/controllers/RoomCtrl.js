const validator = require('validator');

const roomModel = require('../models/RoomModel');

let validationError = {
  name:'ValidationError',
  errors:{}
};


/*******************
 *  Open
 *  @param: user2 = { idx, id, nickname, avatar }
 ********************/
exports.open = async (req, res, next) => {
  /* PARAM */
  const user1 = req.userData;
  
  const user2 = {
    idx: req.body.idx || req.params.idx,
    nickname: req.body.nickname || req.params.nickname,
    avatar: req.body.avatar || req.params.avatar || null
  };

  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!user2.idx || user2.idx === null) {
    isValid = false;
    validationError.errors.idx = { message : "User Idx is required" };
  }

  if (!user2.nickname || validator.isEmpty(user2.nickname)) {
    isValid = false;
    validationError.errors.nickname = { message : "User Nickname is required" };
  }
console.log(req.body);
  console.log(validationError); 

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. DB에 저장하기
  let result = '';
  try {
    const roomData = {
      user1, user2
    };

    result = await roomModel.open(roomData);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 3. 저장 성공
  const respond = {
    status: 201,
    message : "Create Direct Mesesage Room Successfully",
    result
  };
  return res.status(201).json(respond);
}

/*******************
 *  selectAll
 *  @param: page
 ********************/
exports.selectAll = async (req, res, next) => {  
  /* PARAM */
  const userIdx = req.userData.idx;
  const page = req.body.page || req.params.page;

  // 1. DB에서 끌고 오기
  let result = '';
  try {
    result = await roomModel.selectAll(userIdx, page);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 2. 조회 성공
  const respond = {
    status: 200,
    message : "Select Rooms Successfully",
    result
  };
  return res.status(200).json(respond);
};



/*******************
 *  close
 *  @param: roomIdx
 ********************/
exports.close = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const roomIdx = req.body.idx || req.params.idx;
  
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!roomIdx || validator.isEmpty(roomIdx)) {
    isValid = false;
    validationError.errors.roomIdx = { message : "Room Index is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. DB에서 처리하기
  try {
    await roomModel.close(userIdx, roomIdx);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  // 3. 삭제 성공
  const respond = {
    status: 201,
    message : "Close Room Successfully"
  };
  return res.status(201).json(respond);
};



/*******************
 *  toggleAble
 *  @param: roomIdx
 ********************/
exports.toogleAble = async (req, res, next) => {
  /* PARAM */
  const roomIdx = req.body.idx || req.params.idx;

  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!roomIdx || validator.isEmpty(roomIdx)) {
    isValid = false;
    validationError.errors.roomIdx = { message : "Room Index is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. DB에서 처리하기
  let response = '';
  try {
    response = await roomModel.toogleAble(roomIdx);
  } catch (error) {
    // TODO 에러 잡았을때 응답메세지, 응답코드 수정할것
    return next(error);
  }

  let message = '';
  if (response.enable === 1) {
    message = "Enable Room Successfully";
  } else if (response.enable === 0) {
    message = "Disable Room Successfully";
  }

  // 3. 활성화/비활성화 성공
  const respond = {
    status: 201,
    message,
    result: {
      idx: response.idx,
      users: response.users,
      enable: response.enable       
    }
  };
  return res.status(201).json(respond);
}