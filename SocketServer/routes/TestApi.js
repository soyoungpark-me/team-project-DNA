const authCtrl = require('../controllers/AuthCtrl');
const testCtrl = require('../controllers/TestCtrl');

module.exports = (router) => {
  /* 좋아요 추가 or 해제 */
    router.route('/test/like/message/:idx')
    .get(authCtrl.auth, testCtrl.like);  
    
  /* 메시지 생성 테스트용 */
  router.route('/test/message')
    .post(authCtrl.auth, testCtrl.save);

  /* DM 생성 테스트용 */
  router.route('/test/dm')
    .post(authCtrl.auth, testCtrl.savedm);

  return router;
}