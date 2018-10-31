const userCtrl = require('../controllers/UserCtrl');
const authCtrl = require('../controllers/AuthCtrl');

module.exports = (router) => {
  router.route('/users/register')           
    .post(userCtrl.register);                  // 회원가입

  router.route('/user/:idx')               
    .get(authCtrl.auth, userCtrl.select)       // 상세정보 조회
    .put(authCtrl.auth, userCtrl.update);      // 유저 정보 수정  
    
  return router;
};