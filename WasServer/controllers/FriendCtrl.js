const validator = require('validator');
var fetch = require("node-fetch");

const friendModel = require('../models/FriendModel');
const errorCode = require('../utils/error').code;

let validationError = {
  name:'ValidationError',
  errors:{}
};

/*******************
 *  send request
 *  @param: idx
 *  TODO request friend
 *  TODO 친구 요청
 ********************/
exports.sendReq = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const receiverIdx = req.body.receiverIdx || req.params.receiverIdx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!receiverIdx || receiverIdx === null) {
    isValid = false;
    validationError.errors.receiverIdx = { message : "receiverIDX is required" };
  }

  if(userIdx == receiverIdx) {
    isValid = false;
    validationError.errors.receiverIdx = { message : "userIdx is same with receiverIdx"};
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.sendReq(userIdx, receiverIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 친구요청 성공 시 */
  const respond = {
    status: 201,
    message : "Send Friend Request Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  accept request
 *  @param: idx
 *  TODO accept request
 *  TODO 친구 요청 수락하기(친구되기)
 ********************/
exports.accReq = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const targetIdx = req.body.targetIdx || req.params.targetIdx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!targetIdx || targetIdx === null) {
    isValid = false;
    validationError.errors.targetIdx = { message : "targetIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await friendModel.accReq(userIdx, targetIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 친구 추가 성공 시 채팅방을 개설해야 합니다 */
  let response;
  let status;

  fetch(process.env.socketServer + "/room", {
    method: "POST",
    body: JSON.stringify({
      idx: result.idx,
      nickname: result.nickname,
      avatar: result.avatar
    }),
    headers: {"token": req.headers.token, 'Content-Type': 'application/json' },
    withCredentials: true,
    mode: 'no-cors'
  })
  .then((result) => {
    if (result.status === 201) {
      status = 201;
    } else {
      status = 500;
    }
  })
  .catch((err) => {
    console.log(err);
    status = 500;
  })
  .then(() => {
    if (status === 500) {
      /* 채팅방 개설 실패 */
      response = {
        status: 500,
        message : "Failed to open new Conversation Room"
      };
    } else if (status === 201) {
      /* 친구추가 성공 시 */
      response = {
        status: 201,
        message : "Add Friend And Conversation Room is opened Successfully",
        result
      };
    }

    return res.status(status).json(response);
  });
};

/*******************
 *  Delete request
 *  @param: idx
 *  TODO delete friend request
 *  TODO 친구 요청 삭제
 ********************/
exports.delReq = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const targetIdx = req.body.targetIdx || req.params.targetIdx;
  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!targetIdx || targetIdx === null) {
    isValid = false;
    validationError.errors.targetIdx = { message : "targetIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.delReq(userIdx, targetIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 삭제 성공 시 */
  const respond = {
    status: 201,
    message : "Delete Friend Request Successfully",
    result
  };
  return res.status(201).json(respond);
};

/*******************
 *  Show Request list
 *  @param: idx
 *  TODO show friend request
 *  TODO 친구 요청 리스트 조회
 ********************/
exports.showReqList = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }
  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.showReqList(userIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Show Friend Request Successfully",
    result
  };
  return res.status(200).json(respond);
};

/*******************
 *  Show Send list
 *  @param: idx
 *  TODO show friend send
 *  TODO ㄴㅐ가보낸 친구 요청 리스트 조회
 ********************/
exports.showSendList = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }
  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.showSendList(userIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Show Friend Request Successfully",
    result
  };
  return res.status(200).json(respond);
};

/*******************
 *  Show Wait
 *  @param: idx
 *  TODO show friend wait
 *  TODO 친구 대기 여부 조회
 ********************/
exports.showWait = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const targetIdx = req.body.targetIdx || req.params.targetIdx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!targetIdx || targetIdx === null) {
    isValid = false;
    validationError.errors.targetIdx = { message : "targetIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.showWait(userIdx, targetIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Show Friend Wait Successfully, you've sent a request already.",
    result
  };
  return res.status(200).json(respond);
};

/*******************
 *  Delete
 *  @param: idx
 *  TODO delete friend
 *  TODO 친구 삭제
 ********************/
exports.delete = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const friendIdx = req.body.friendIdx || req.params.friendIdx;
  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!friendIdx || friendIdx === null) {
    isValid = false;
    validationError.errors.friendIdx = { message : "friendIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.delete(userIdx, friendIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 삭제 성공 시 */
  const respond = {
    status: 201,
    message : "Delete Friend Successfully",
    result
  };
  return res.status(201).json(respond);
};

/*******************
 *  Show
 *  @param: idx
 *  TODO show friend
 *  TODO 친구 조회
 ********************/
exports.show = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }
  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.show(userIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Show Friend Successfully",
    result
  };
  return res.status(200).json(respond);
};

/*******************
 *  Search
 *  @param: idx
 *  TODO search user for friend
 *  TODO 친구 찾기
 ********************/
exports.search = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const targetIdx = req.body.targetIdx || req.params.targetIdx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!targetIdx || targetIdx === null) {
    isValid = false;
    validationError.errors.targetIdx = { message : "targetIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await friendModel.search(userIdx, targetIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Search User Successfully",
    result
  };
  return res.status(200).json(respond);
};
