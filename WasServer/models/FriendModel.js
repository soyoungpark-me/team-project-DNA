const mysql = global.utils.mysql;
const redis = global.utils.redis;


/*******************
 *  send friend request
 *  @param: userData = { idx }
 ********************/
exports.sendReq = (userIdx, receiverIdx) => {
  // 1. 친구여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM friends
                  WHERE (user1_idx = ? AND user2_idx = ?) OR (user1_idx = ? AND user2_idx = ?)`;

    mysql.query(sql, [userIdx, receiverIdx, receiverIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length !== 0) {
          reject(30400);
        } else {
          resolve();
        }
      }
    });
  })
  .then(() => {
    // 2. 친구 대기여부 확인
    return new Promise((resolve, reject) => {
      const sql = `SELECT *
                    FROM friend_wait
                    WHERE (sender_idx = ? AND receiver_idx = ?) OR (sender_idx = ? AND receiver_idx = ?)`;
      mysql.query(sql, [userIdx, receiverIdx, receiverIdx, userIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.length === 0) {
              resolve();
            } else {
              reject(34400);
            }
          }
      });
    });
  })
  .then(() => {
    // 3. DB에 정보 삽입하기
    return new Promise((resolve, reject) => {
      const sql = `INSERT INTO friend_wait (sender_idx, receiver_idx)
                          VALUES     (?, ?)`;
      mysql.query(sql, [userIdx, receiverIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(22500);
            }
          }
      });
    });
  });
};

/*******************
 *  accept friend request
 *  @param: userData = { idx }
 ********************/
exports.accReq = (userIdx, senderIdx) => {
  // 1. 친구 요청 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM friend_wait
                  WHERE (receiver_idx = ? AND sender_idx = ?)`;
    mysql.query(sql, [userIdx, senderIdx], (err, rows) => {
      if (err) {
        reject (err);
      } else {
        if (rows.length !== 0) {
          resolve();
        } else {
          reject(33400);
        }
      }
    });
  })
  .then(() => {
    // 2. DB에 정보 삭제하기
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM friend_wait
                    WHERE (receiver_idx = ? AND sender_idx = ?)`;
      mysql.query(sql, [userIdx, senderIdx], (err, rows) => {
        if (err) {
          reject (err);
        } else {
          if (rows.affectedRows === 1) {
            resolve();
          } else {
            reject(22500);
          }
        }
      });
    });
  })
  .then(() => {
  // 3. DB에 정보 삽입하기
    return new Promise((resolve, reject) => {
      const sql = `INSERT INTO friends (user1_idx, user2_idx)
                    VALUES     (?, ?)`;
      mysql.query(sql, [userIdx, senderIdx], (err, rows) => {
        if (err) {
          reject (err);
        } else {
          if (rows.affectedRows === 1) {
            resolve();
          } else {
            reject(22500);
          }
        }
      });
    });
  })
  .then(() => {
    return new Promise((resolve, reject) => {
      const sql = `SELECT idx, nickname, avatar
                   FROM users
                  WHERE idx = ?`;
      mysql.query(sql, senderIdx, (err, rows) => {
        if (err) {
          reject (err);
        } else {
          if (rows.length > 0) {
            resolve(rows[0]);
          } else {
            reject(20400);
          }
        }
      });
    });
  });
};

/*******************
 *  Delete friend request
 *  @param: idx
 ********************/
exports.delReq = (userIdx,targetIdx) => {
  // 1. 요청여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM friend_wait
                  WHERE (sender_idx = ? AND receiver_idx = ?) OR (sender_idx = ? AND receiver_idx = ?)`;

    mysql.query(sql, [userIdx, targetIdx, targetIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(33400);
        } else {
          resolve();
        }
      }
    });
  })
  .then(() => {
    // 2. DB에서 정보 삭제하기
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM friend_wait
                    WHERE (sender_idx = ? AND receiver_idx = ?) OR (sender_idx = ? AND receiver_idx = ?)`;
      mysql.query(sql, [userIdx, targetIdx, targetIdx, userIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(22500);
            }
          }
      });
    });
  });
};

/*******************
 *  Show req list
 *  @param: userData = { idx }
 ********************/
 exports.showReqList = (userIdx) => {
   // 1. 친구여부 확인
   return new Promise((resolve, reject) => {
     const sql = `SELECT *
                   FROM friend_wait
                   WHERE sender_idx = ? OR receiver_idx = ?`;

     mysql.query(sql, [userIdx, userIdx], (err, rows) => {
       if (err) {
         reject(err);
       } else {
         if (rows.length === 0) {
           reject(33400);
         } else {
           resolve(rows);
         }
       }
     });
   });
 };

 /*******************
  *  show wait
  *  @param: idx
  ********************/
 exports.showWait = (userIdx, targetIdx) => {
   // 1. 친구대기상태인지 확인
   return new Promise((resolve, reject) => {
     const sql = `SELECT *
                   FROM friend_wait
                   WHERE (sender_idx = ? AND receiver_idx = ?) OR (sender_idx = ? AND receiver_idx = ?)`;

     mysql.query(sql, [userIdx, targetIdx, targetIdx, userIdx], (err, rows) => {
       if (err) {
         reject(err);
       } else {
         if (rows.length === 0) {
           reject(33400);
         } else {
           resolve(rows);
         }
       }
     });
   });
 };

/*******************
 *  Delete friend
 *  @param: idx
 ********************/
exports.delete = (userIdx,friendIdx) => {
  // 1. 친구여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM friends
                  WHERE (user1_idx = ? AND user2_idx = ?) OR (user1_idx = ? AND user2_idx = ?)`;

    mysql.query(sql, [userIdx, friendIdx, friendIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(31400);
        } else {
          resolve(rows);
        }
      }
    });
  })
  .then(() => {
    // 2. DB에서 정보 삭제하기
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM friends
                    WHERE (user1_idx = ? AND user2_idx = ?) OR (user1_idx = ? AND user2_idx = ?)`;
      mysql.query(sql, [userIdx, friendIdx, friendIdx, userIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(22500);
            }
          }
      });
    });
  });
};

/*******************
 *  Show friends
 *  @param: idx
 ********************/
exports.show = (userIdx) => {
  // 1. 친구여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM friends
                  WHERE user1_idx = ? OR user2_idx = ?`;

    mysql.query(sql, [userIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(32400);
        } else {
          resolve(rows);
        }
      }
    });
  });
};
