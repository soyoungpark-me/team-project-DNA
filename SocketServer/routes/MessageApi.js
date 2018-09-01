const authCtrl = require('../controllers/AuthCtrl');
const messageCtrl = require('../controllers/MessageCtrl');

module.exports = (router) => {  
  /* 특정 메시지 세부 내용 조회 */
  router.route('/message/:idx')
    .get(authCtrl.auth, messageCtrl.selectOne);
  
  /* 특정 반경 내의 메시지 리스트 조회 */
  router.route('/messages')                // All
    .post(authCtrl.auth, messageCtrl.selectCircle);
  router.route('/messages/:page')          // Paged
    .post(authCtrl.auth, messageCtrl.selectCircle);

  router.route('/best')
    .post(authCtrl.auth, messageCtrl.best);

  /* 모든 메시지 리스트 조회 */
  // router.route('/messages')               // All
  //   .get(authCtrl.auth, messageCtrl.selectAll);
  // router.route('/messages/:page')         // Paged
  //   .get(authCtrl.auth, messageCtrl.selectAll);

  return router;
};