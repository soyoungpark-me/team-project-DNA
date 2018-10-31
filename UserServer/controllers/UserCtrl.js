const validator = require('validator');

const userModel = require('../models/UserModel');
const helpers = require('../utils/helpers');
const errorCode = require('../utils/error').code;

let validationError = {
  name:'ValidationError',
  errors:{}
};


/*******************
 *  Register
 *  @param: id, password, confirm_password, email, nickname, avatar, description
 *  TODO validation
 *  TODO 이미지 등록
 ********************/
exports.register = async (req, res, next) => {   
  /* PARAM */
  const id = req.body.id || req.params.id;
  const password = req.body.password || req.params.password;
  const confirm_password = req.body.confirm_password || req.params.confirm_password;
  const email = req.body.email || req.params.email;
  const avatar = req.body.avatar || req.params.avatar || null;
  const description = req.body.description || req.params.description || null;
  let nickname = req.body.nickname || req.params.nickname;
  

  /* 1. 유효성 체크하기 */
  let validpassword;
  let isValid = true;
  
  if (!id || validator.isEmpty(id)) {
    isValid = false;
    validationError.errors.id = { message : "ID is required" };
  }

  // nickname을 입력하지 않으면 자동으로 id와 같게 등록하도록 합니다
  if (!nickname || validator.isEmpty(nickname)) {
    nickname = id;
  }

  if (!email || validator.isEmpty(email)) {
    isValid = false;
    validationError.errors.email = { message : "Email is required" };
  }

  if (!password || validator.isEmpty(password)) {
    isValid = false;
    validationError.errors.password = { message : "Password is required" };
  }
  
  // 입력한 비밀번호가 서로 일치하는지 체크
  if (password !== confirm_password) {
    isValid = false;
    validationError.errors.password = { message : "Passwords do not match" };
  } else {
    validpassword = password;
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  // 2. 아바타용 이미지를 업로드했는지 체크
  // TODO 이미지 업로드 처리하기
  let image;
  if (!req.file) {
    image = null;
  } else {
    image = req.file.location;
  }

  // 3. 결과 암호화해서 DB에 저장하기
  let result = '';
  try {
    const encodedPassword = helpers.doCypher(validpassword);
    const userData = {
      id,
      password: encodedPassword.password,
      nickname,
      email,
      avatar,
      description,
      salt: encodedPassword.newSalt
    };
    result = await userModel.register(userData);
  } catch (err) {
    console.log(err);
    return res.status(errorCode[err].status)
              .json(errorCode[err].contents);
  }
  // 4. 등록 성공
  const respond = {
    status: 201,
    message : "Register Successfully",
    result: result[0]
  };
  return res.status(201).json(respond);
};


/*******************
 *  Login
 *  @param: id, password
 ********************/
exports.login = async (req, res, next) => {
  /* PARAM */
  const id = req.body.id || req.params.id;
  const password = req.body.password || req.params.password;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!id || validator.isEmpty(id)) {
    isValid = false;
    validationError.errors.id = { message : 'ID is required' };
  }

  if (!password || validator.isEmpty(password)) {
    isValid = false;
    validationError.errors.password = { message:'Password is required' };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    // TODO 회원이 없을 경우
    let getSalt;
    try {
      getSalt = await userModel.getSalt(id);
    } catch (err) {
      console.log(err);
      return res.status(errorCode[err].status)
                .json(errorCode[err].contents);
    }    

    const decodedPassword = helpers.doCypher(password, getSalt.salt).password;
    const userData = {
      id: id,
      password: decodedPassword
    };

    result = await userModel.login(userData);

  } catch (err) {
    console.log(err);
    return res.status(errorCode[err].status)
              .json(errorCode[err].contents);
  }

  /* 로그인 성공 시 */
  const respond = {
    status: 200,
    message : "Login Successfully",
    result
  };
  return res.status(200).json(respond);  
};



/*******************
 *  Select
 *  @param: idx
 ********************/
exports.select = async (req, res, next) => {
  /* PARAM */
  const idx = req.body.idx || req.params.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!idx || validator.isEmpty(idx)) {
    isValid = false;
    validationError.errors.idx = { message : 'Idx is required' };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await userModel.select(idx);
  } catch (err) {
    console.log(err);
    return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Select User Successfully",
    result
  };
  return res.status(200).json(respond);  
};



/*******************
 *  Update
 *  @param: idx, password, new_password, 
 *    confirm_password, nickname, avatar, description
 *  TODO 이미지 등록
 ********************/
exports.update = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;
  const password = req.body.password || req.params.password || null;
  const newPassword = req.body.new_password || req.params.new_password || null;
  const confirmPassword = req.body.confirm_password || req.params.confirm_password || null;
  const nickname = req.body.nickname || req.params.nickname || null;
  const avatar = req.body.avatar || req.params.avatar || null;
  const description = req.body.description || req.params.description || null;

  /* 비밀번호까지 바꿀건지 */
  let changePassword = false;
  /* 유효성 체크하기 */
  let isValid = true;

  if (newPassword && !validator.isEmpty(newPassword)) { // 새 비밀번호가 존재한다면
    changePassword = true;

    // 1. 새 비밀번호와 확인 비밀번호가 일치하는지 체크
    if (!confirmPassword || !validator.equals(newPassword, confirmPassword)) {
      isValid = false;
      validationError.errors.password = { message : 'Passwords do not match' };    
    }

    // 2. 현재 비밀번호와 입력한 비밀번호가 같은지 체크
    const userData = {
      id: req.userData.id,
      password
    }
    
    if (isValid) {
      if (!password || validator.isEmpty(password)) {
        isValid = false;
        validationError.errors.idx = { message : 'Please enter the original password' };
      }
      if (isValid && await userModel.passwordCheck(userData) !== true) {
        isValid = false;
        validationError.errors.idx = { message : 'Wrong Password' };
      }
    }    
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let encodedPassword;
  // 새 비밀번호 해싱
  if (changePassword) {
    encodedPassword = helpers.doCypher(newPassword);
  }

  const updateData = {
    idx, nickname, avatar, description, encodedPassword
  }
  let result = '';

  try {
    result = await userModel.update(updateData, changePassword);
  } catch (err) {
    console.log(err);
    return next(err);
    // return res.json(errorCode[err]);
  }

  /* 수정 성공 시 */
  const respond = {
    status: 201,
    message : "Update User Successfully",
    result
  };
  return res.status(201).json(respond);  
};


/*******************
 *  block
 *  @param: blockUseridx
 ********************/
exports.block = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const blockUseridx = req.body.idx || req.params.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!blockUseridx || validator.isEmpty(blockUseridx)) {
    isValid = false;
    validationError.errors.blockUseridx = 
      { message : 'User (to Block) Index is required' };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await userModel.block(userIdx, blockUseridx);
  } catch (err) {
    console.log(err);
    return next(err);
    // return res.json(errorCode[err]);
  }

  let respond = {
    status: 201,
    message : "",
    result
  };
  if (result === 0) {
    /* 삭제 성공 시 */
    respond.message = "Cancel Block User Successfully";
  } else {
    /* 차단 추가 성공 시 */
    respond.message = "Block User Successfully";
  }

  return res.status(201).json(respond);  
};



/*******************
 *  selectBlock
 *  @param: idx
 ********************/
exports.selectBlock = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;

  let result = '';

  try {
    result = await userModel.selectBlock(idx);
  } catch (err) {
    console.log(err);
    return next(err);
    // return res.json(errorCode[err]);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 200,
    message : "Select Block Users Successfully",
    result
  };
  return res.status(200).json(respond);  
};



/*******************
 *  report
 *  @param: reportUseridx
 ********************/
exports.report = async (req, res, next) => {
  /* PARAM */
  const userIdx = req.userData.idx;
  const reportUseridx = req.body.idx || req.params.idx;

  /* 유효성 체크하기 */
  let isValid = true;

  if (!reportUseridx) {
    isValid = false;
    validationError.errors.reportUseridx = 
      { message : 'User (to Report) Index is required' };
  }

  if (parseInt(userIdx) === parseInt(reportUseridx)) {
    isValid = false;
    validationError.errors.reportUseridx = 
      { message : 'Two indexes are identical and cannot be reported' };
  }

  if (!isValid) return res.status(400).json(validationError);
  /* 유효성 체크 끝 */

  let result = '';

  try {
    result = await userModel.report(userIdx, reportUseridx);
  } catch (err) {
    console.log(err);
    return res.status(errorCode[err].status)
              .json(errorCode[err].contents);
  }

  const respond = {
    status: 201,
    message : "Report Successfully",
    result
  };

  return res.status(respond.status).json(respond);  
};


/*******************
 *  SelectPoints
 *  @param: idx
 ********************/
exports.selectPoints = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;

  let result = '';

  try {
    result = await userModel.selectPoints(idx);
  } catch (err) {
    console.log(err);
    return next(err);
  }

  /* 조회 성공 시 */
  const response = {
    status: 200,
    message : "Select Points Successfully",
    result
  };
  return res.status(200).json(response);  
};


/*******************
 *  reducePoints
 *  @param: idx
 ********************/
exports.reducePoints = async (req, res, next) => {
  /* PARAM */
  const idx = req.userData.idx;

  let result = '';

  try {
    result = await userModel.reducePoints(idx);
  } catch (err) {
    console.log(err);
    return next(err);
  }

  /* 조회 성공 시 */
  const respond = {
    status: 201,
    message : "Reduce Points Successfully",
    result
  };
  return res.status(201).json(respond);  
};