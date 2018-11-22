const userCtrl = require('../controllers/UserCtrl');
const authCtrl = require('../controllers/AuthCtrl');
const postingCtrl = require('../controllers/PostingCtrl')

module.exports = (router) => {
  router.route('/posting/')
    .post(authCtrl.auth, postingCtrl.write);                  // 포스팅 글쓰기

  router.route('/posting/:postingIdx')
    .delete(authCtrl.auth, postingCtrl.delete)                // 포스팅 삭제
    .put(authCtrl.auth, postingCtrl.update);                 // 포스팅 수정

  router.route('/posting/showAll/')
    .get(authCtrl.auth, postingCtrl.showAll);                // 포스팅 위치 조회

  router.route('/posting/show/:postingIdx')
    .get(postingCtrl.show);                                // 포스팅 조회

  router.route('/posting/like/:postingIdx')
    .post(authCtrl.auth, postingCtrl.like)                // 포스팅 좋아요
    .delete(authCtrl.auth, postingCtrl.unlike);              // 포스팅 좋아요취소

  router.route('/posting/reply/:postingIdx')
    .post(authCtrl.auth, postingCtrl.reply)              // 포스팅 댓글쓰기
    .delete(authCtrl.auth, postingCtrl.dreply);              // 포스팅 댓글삭제

  router.route('/posting/bookmark/:postingIdx')
    .post(authCtrl.auth, postingCtrl.bookmark)              // 포스팅 북마크 하기
    .delete(authCtrl.auth, postingCtrl.dbookmark)              // 포스팅 북마크 취소하기

  router.route('/posting/bookmark/')
    .get(authCtrl.auth, postingCtrl.showBookmark);              // 포스팅 북마크 조회하기

  router.route('/posting/showMine/')
    .get(authCtrl.auth, postingCtrl.showMyPost);              // 내 포스팅 조회하기

  return router;
};
