const authCtrl = require('../controllers/AuthCtrl');
const roomCtrl = require('../controllers/RoomCtrl');
const dmCtrl = require('../controllers/DMCtrl');

module.exports = (router) => {
  /* 채팅방 조회 */
  router.route('/rooms')                     // All
    .get(authCtrl.auth, roomCtrl.selectAll)    
  router.route('/rooms/:page')               // Paged
    .get(authCtrl.auth, roomCtrl.selectAll)

  /* 채팅방 생성 */
  router.route('/room')
    .post(authCtrl.auth, roomCtrl.open)
  
  /* 채팅방 삭제 */
  router.route('/room/:idx')
    .delete(authCtrl.auth, roomCtrl.close);

  /* DM 조회 */
  router.route('/room/:idx/messages')       // All
    .get(authCtrl.auth, dmCtrl.selectAll);
  router.route('/room/:idx/messages/:page') // Paged
    .get(authCtrl.auth, dmCtrl.selectAll);    

  return router;
};