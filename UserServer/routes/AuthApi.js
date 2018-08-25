const authCtrl = require('../controllers/AuthCtrl');

module.exports = (router) => {
  router.route('/auth').get(authCtrl.auth);                 // 토큰 인증
  router.route('/auth/refresh').get(authCtrl.refresh);      // 토큰 재발급

  return router;
};