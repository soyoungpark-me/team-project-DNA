const authCtrl = require('../controllers/AuthCtrl');
const messageCtrl = require('../controllers/MessageCtrl');

module.exports = (router) => {  
  /* 특정 메시지 세부 내용 조회 */
  router.route('/message/:idx')
    .get(authCtrl.auth, messageCtrl.selectOne);
  
  /* 특정 반경 내의 메시지 리스트 조회 */
  router.route('/message')                // All
    .post(authCtrl.auth, messageCtrl.selectCircle);
  router.route('/message/:page')          // Paged
    .post(authCtrl.auth, messageCtrl.selectCircle);

  /* 모든 메시지 리스트 조회 */
  router.route('/messages')               // All
    .get(authCtrl.auth, messageCtrl.selectAll);
  router.route('/messages/:page')         // Paged
    .get(authCtrl.auth, messageCtrl.selectAll);

  /* 좋아요 추가 or 해제 */
  router.route('/like/message/:idx')
    .get(authCtrl.auth, messageCtrl.like);
    
    
  /* 메시지 생성 테스트용 */
  router.route('/test/message')
    .post(authCtrl.auth, messageCtrl.testsave);

  return router;
};