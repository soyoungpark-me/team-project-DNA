const userCtrl = require('../controllers/UserCtrl');
const authCtrl = require('../controllers/AuthCtrl');

module.exports = (router) => {
  router.route('/user/:idx')               
    .get(authCtrl.auth, userCtrl.select)       // 상세정보 조회
    .put(authCtrl.auth, userCtrl.update);      // 유저 정보 수정
    
  router.route('/users/login')              
    .post(userCtrl.login);                     // 로그인

  router.route('/users/register')           
    .post(userCtrl.register);                  // 회원가입

  router.route('/users/block')
    .get(authCtrl.auth, userCtrl.selectBlock)  // 차단 리스트 보기

  router.route('/users/block/:idx')
    .get(authCtrl.auth, userCtrl.block)        // 차단하기

  router.route('/users/report/:idx')           
    .get(authCtrl.auth, userCtrl.report);      // 신고하기

  router.route('/users/point/:idx')
    .get(authCtrl.auth, userCtrl.selectPoints) // 포인트 조회하기
    .put(authCtrl.auth, userCtrl.reducePoints) // 포인트 차감하기
  
  return router;
};