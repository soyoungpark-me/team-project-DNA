const validator = require('validator');
var fetch = require("node-fetch");

const postingModel = require('../models/PostingModel');
const errorCode = require('../utils/error').code;

let validationError = {
  name:'ValidationError',
  errors:{}
};

/*******************
 *  Write
 *  @param: useridx, loc, date, contents
 *  TODO write posting
 *  TODO 포스팅 글쓰기
 ********************/
exports.write = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const userLng = req.body.userLng || req.params.userLng;
  const userLat = req.body.userLat || req.params.userLat;
  const date = req.body.date || req.params.date;
  const ptitle = req.body.ptitle || req.params.ptitle;
  const pcontents = req.body.pcontents || req.params.pcontents;
  const onlyme = req.body.onlyme || req.params.onlyme;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!userLng|| userLng === null ) {
    isValid = false;
    validationError.errors.userLng= { message : "userLng is required" };
  }

  if (!userLat || userLat === null ) {
    isValid = false;
    validationError.errors.userYLoc= { message : "userLat is required" };
  }

  if (!ptitle || ptitle === null) {
    isValid = false;
    validationError.errors.ptitle = { message : "ptitle is required" };
  }

  if (!pcontents || pcontents === null) {
    isValid = false;
    validationError.errors.pcontents = { message : "pconetnt is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.write(userIdx, userLng, userLat, date, pcontents, onlyme);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Write Posting Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  Delete
 *  @param: useridx, postingidx
 *  TODO delete posting
 *  TODO 포스팅삭제
 ********************/
exports.delete = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx = { message : "postingIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await postingModel.delete(userIdx, postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 삭제 성공 시 */
  const respond = {
    status: 201,
    message : "Delete Posting Successfully",
    result
  };
  return res.status(201).json(respond);
};

/*******************
 *  Show
 *  @param: postingidx
 *  TODO show posting
 *  TODO 포스팅 조회
 ********************/
exports.show = async (req, res, next) => {
  /* PARAM */
  const postingIdx = req.body.postingIdx || req.params.postingIdx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx = { message : "postingIDX is required" };
  }
  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await postingModel.show(postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Show Posting Successfully",
    result
  };
  return res.status(200).json(respond);
};

/*******************
 *  update
 *  @param: useridx, pstingidx, pcontents
 *  TODO update posting
 *  TODO 포스팅 수정
 ********************/
exports.update = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  const pcontents = req.body.pcontents || req.params.pcontents;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx = { message : "postingIDX is required" };
  }

  if (!pcontents || pcontents === null) {
    isValid = false;
    validationError.errors.pcontents = { message : "pcontent is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.update(userIdx, postingIdx, pcontents);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 포스팅 수정 성공 시 */
  const respond = {
    status: 201,
    message : "Update posting Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  like
 *  @param: useridx, postingidx, plikes
 *  TODO like posting
 *  TODO 포스팅 공감하기
 ********************/
exports.like = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx = { message : "postingIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await postingModel.like(userIdx, postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 삭제 성공 시 */
  const respond = {
    status: 201,
    message : "Like posting Successfully",
    result
  };
  return res.status(201).json(respond);
};

/*******************
 *  unlike
 *  @param: useridx, postingidx, plikes
 *  TODO unlike posting
 *  TODO 포스팅 공감 취소하기
 ********************/
exports.unlike = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  /* 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx = { message : "postingIDX is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await postingModel.unlike(userIdx, postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 삭제 성공 시 */
  const respond = {
    status: 201,
    message : "Unlike posting Successfully",
    result
  };
  return res.status(201).json(respond);
};

/*******************
 *  Reply
 *  @param: useridx, postingIdx
 *  TODO reply to posting
 *  TODO 포스팅 댓글쓰기
 ********************/
exports.reply = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  const rcontents = req.body.rcontents || req.params.rcontents;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx= { message : "postingIdx is required" };
  }

  if (!rcontents || rcontents === null) {
    isValid = false;
    validationError.errors.rcontents= { message : "rcontents is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.reply(userIdx, postingIdx, rcontents);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Reply to Posting Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  Delete Reply
 *  @param: useridx, replyIdx
 *  TODO delete reply from posting
 *  TODO 포스팅 댓글 삭제하기
 ********************/
exports.dreply = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const replyIdx = req.body.replyIdx || req.params.replyIdx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!replyIdx || replyIdx === null) {
    isValid = false;
    validationError.errors.postingIdx= { message : "replyIdx is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.dreply(userIdx, replyIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Delete Reply from Posting Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  Bookmark
 *  @param: useridx, postingIdx
 *  TODO posting bookmark
 *  TODO 포스팅 북마크하기
 ********************/
exports.bookmark = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx= { message : "postingIdx is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.bookmark(userIdx, postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Bookmark Posting Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  Delete Bookmark
 *  @param: useridx, postingIdx
 *  TODO posting bookmark
 *  TODO 포스팅 북마크 취소하기
 ********************/
exports.dbookmark = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const postingIdx = req.body.postingIdx || req.params.postingIdx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }

  if (!postingIdx || postingIdx === null) {
    isValid = false;
    validationError.errors.postingIdx= { message : "postingIdx is required" };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.dbookmark(userIdx, postingIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Delete Bookmark Successfully",
    result
  };
  return res.status(201).json(respond);

};

/*******************
 *  Show Bookmark
 *  @param: useridx, postingIdx
 *  TODO show posting bookmark
 *  TODO 포스팅 북마크 목록 조회하기
 ********************/
exports.showBookmark = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  /* 1. 유효성 체크하기 */
  let isValid = true;

  if (!userIdx || userIdx === null) {
    isValid = false;
    validationError.errors.userIdx = { message : "userIDX is required" };
  }


  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */
  let result = '';

  try {
    result = await postingModel.showBookmark(userIdx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }
  const respond = {
    status: 201,
    message : "Show Bookmark Successfully",
    result
  };
  return res.status(201).json(respond);

};
