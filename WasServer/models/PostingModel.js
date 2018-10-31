const mysql = global.utils.mysql;
const redis = global.utils.redis;

/*******************
 *  Write
 *  @param: useridx, userLoc, date, pcontents
 ********************/
exports.write = (userIdx, userLng, userLat, date, ptitle, pcontents, onlyme) => {
  return new Promise((resolve, reject) => {
    const sql = `INSERT INTO posting (writer_idx, postLng, postLat, posting_date, title, contents, onlyme)
                        VALUES     (?, ?, ?, ?, ?, ?, ?)`;

    mysql.query(sql, [userIdx, userLng, userLat, date, ptitle, pcontents, onlyme], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 1) {
          resolve(rows);
        } else {
          reject(40400);
        }
      }
    });
  });
};

/*******************
 *  Delete
 *  @param: userIdx, postingIdx
 ********************/
exports.delete = (userIdx, postingIdx) => {
  // 1. 작성자여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM posting
                  WHERE (writer_idx = ? AND posting_idx = ?)`;

    mysql.query(sql, [userIdx, postingIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(41400);
        } else {
          resolve();
        }
      }
    });
  })
  .then(() => {
    // 2. DB에서 정보 삭제하기-posting
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM posting
                    WHERE (writer_idx = ? AND posting_idx = ?)`;
      mysql.query(sql, [userIdx, postingIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(42400);
            }
          }
      });
    });
  })
  .then(() => {
    // 3. DB에서 정보 삭제하기-likes
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM posting_likes
                    WHERE posting_idx = ?`;
      mysql.query(sql, postingIdx, (err, rows) => {
          if (err) {
            reject (err);
          } else {
            // if (rows.affectedRows !== 0) {
              resolve(rows);
            // } else {
              // reject(43400);
            // }
          }
      });
    });
  })
  .then(() => {
    // 4. DB에서 정보 삭제하기-reply
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM posting_reply
                    WHERE posting_idx = ?`;
      mysql.query(sql, postingIdx, (err, rows) => {
          if (err) {
            reject (err);
          } else {
            // if (rows.affectedRows !== 0) {
              resolve(rows);
            // } else {
              // reject(50400);
            // }
          }
      });
    });
  })
  .then(() => {
    // 5. DB에서 정보 삭제하기-bookmark
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM posting_bookmark
                    WHERE posting_idx = ?`;
      mysql.query(sql, postingIdx, (err, rows) => {
          if (err) {
            reject (err);
          } else {
            // if (rows.affectedRows !== 0) {
              resolve(rows);
            // } else {
              // reject(52400);
            // }
          }
      });
    });
  });
};

/*******************
 *  Show
 *  @param: postingidx
 ********************/
exports.show = (postingIdx) => {
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM posting
                  WHERE posting_idx = ?`;

    mysql.query(sql, postingIdx, (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(44400);
        } else {
          resolve(rows);
        }
      }
    });
  });
  // 글쓴유저 정보도 보여줘야하나본데,,
  // .then(() => {
  //   const sql = `SELECT users.avatar, users.nickname
  //                 FROM users, posting
  //                 WHERE posting.writer_idx = users.idx`;
  //
  //   mysql.query(sql, (err, rows) => {
  //     if (err) {
  //       reject(err);
  //     } else {
  //       if (rows.length === 0) {
  //         reject(44400);
  //       } else {
  //         resolve(rows);
  //       }
  //     }
  //   });
  // });
};

/*******************
 *  Update posting
 *  @param: useridx, postingidx, pcontents
 ********************/
 exports.update = (userIdx, postingIdx, pcontents) => {
   // 1. 작성자여부 확인
   return new Promise((resolve, reject) => {
     const sql = `SELECT *
                   FROM posting
                   WHERE (writer_idx = ? AND posting_idx = ?)`;

     mysql.query(sql, [userIdx, postingIdx], (err, rows) => {
       if (err) {
         reject(err);
       } else {
         if (rows.length === 0) {
           reject(41400);
         } else {
           resolve();
         }
       }
     });
   })
   .then(() => {
     const sql = `SELECT title, contents
                   FROM posting
                   WHERE posting_idx = ?`;

     mysql.query(sql, postingIdx, (err, rows) => {
       if (err) {
         reject(err);
       } else {
         if (rows.length === 0) {
           reject(41400);
         } else {
           resolve();
         }
       }
     });
   })
   .then(() => {
     // 2. DB에서 정보 수정하기
     return new Promise((resolve, reject) => {
       const sql = `UPDATE posting
                    SET contents = "?"
                    WHERE posting_idx = ?`;
       mysql.query(sql, [pcontents, postingIdx], (err, rows) => {
           if (err) {
             reject (err);
           } else {
             if (rows.affectedRows === 1) {
               resolve(rows);
             } else {
               reject(45400);
             }
           }
       });
     });
   });
 };

/*******************
 *  like posting
 *  @param: useridx, postingidx, plikes
 ********************/
exports.like = (userIdx, postingIdx) => {
  // 1. 공감리스트에 있는지 확인하기
  return new Promise((resolve, reject) => {
    const sql = `SELECT * FROM posting_likes
                  WHERE (posting_idx = ? AND user_idx = ?)`;

    mysql.query(sql, [postingIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length !== 0) {
          reject(46400);
        } else {
          resolve(true);
        }
      }
    });
  })
  .then(() => {
    // 2. 공감하기
    return new Promise((resolve, reject) => {
      const sql = `INSERT INTO posting_likes(posting_idx, user_idx)
                    VALUES (?, ?)`;

      mysql.query(sql, [postingIdx, userIdx], (err, rows) => {
        if (err) {
          reject(err);
        } else {
          if (rows.affectedRows === 0) {
            reject(47400);
          } else {
            resolve(rows);
          }
        }
      });
    });
  })
  .then(() => {
    // 3. 공감수 수정하기
    return new Promise((resolve, reject) => {
      const sql = `UPDATE posting
                   SET likes_cnt = likes_cnt+1
                   WHERE posting_idx = ?`;
      mysql.query(sql, postingIdx, (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(48400);
            }
          }
      });
    });
  });
};

/*******************
 *  Unlike posting
 *  @param: useridx, postingidx, plikes
 ********************/
exports.unlike = (userIdx, postingIdx) => {
  // 1. 공감리스트에 있는지 확인하기
  return new Promise((resolve, reject) => {
    const sql = `DELETE FROM posting_likes
                  WHERE (posting_idx = ? AND user_idx = ?)`;

    mysql.query(sql, [postingIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 0) {
          reject(47400);
        } else {
          resolve(rows);
        }
      }
    });
  })
  .then(() => {
    // 3. 공감수 수정하기
    return new Promise((resolve, reject) => {
      const sql = `UPDATE posting
                   SET likes_cnt = likes_cnt-1
                   WHERE posting_idx = ?`;
      mysql.query(sql, postingIdx, (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(48400);
            }
          }
      });
    });
  });
};

/*******************
 *  Reply
 *  @param: useridx, postingIdx
 ********************/
exports.reply = (userIdx, postingIdx, rcontents) => {
  return new Promise((resolve, reject) => {
    const sql = `INSERT INTO posting_reply (posting_idx, user_idx, reply_contents)
                        VALUES     (?, ?, ?)`;

    mysql.query(sql, [postingIdx, userIdx, rcontents], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 1) {
          resolve(rows);
        } else {
          reject(50400);
        }
      }
    });
  });
};

/*******************
 *  Delete reply
 *  @param: userIdx, replyIdx
 ********************/
exports.dreply = (userIdx, replyIdx) => {
  // 1. 작성자여부 확인
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM posting_reply
                  WHERE (user_idx = ? AND reply_idx = ?)`;

    mysql.query(sql, [userIdx, replyIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) {
          reject(41400);
        } else {
          resolve();
        }
      }
    });
  })
  .then(() => {
    // 2. DB에서 정보 삭제하기
    return new Promise((resolve, reject) => {
      const sql = `DELETE FROM posting_reply
                    WHERE (user_idx = ? AND reply_idx = ?)`;
      mysql.query(sql, [userIdx, replyIdx], (err, rows) => {
          if (err) {
            reject (err);
          } else {
            if (rows.affectedRows === 1) {
              resolve(rows);
            } else {
              reject(51400);
            }
          }
      });
    });
  });
};

/*******************
 *  Bookmark
 *  @param: useridx, postingIdx
 ********************/
exports.bookmark = (userIdx, postingIdx) => {
  return new Promise((resolve, reject) => {
    const sql = `INSERT INTO posting_bookmark (posting_idx, user_idx)
                        VALUES     (?, ?)`;

    mysql.query(sql, [postingIdx, userIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 1) {
          resolve(rows);
        } else {
          reject(52400);
        }
      }
    });
  });
};

/*******************
 *  Delete Bookmark
 *  @param: useridx, postingIdx
 ********************/
exports.dbookmark = (userIdx, postingIdx) => {
  return new Promise((resolve, reject) => {
    const sql = `DELETE FROM posting_bookmark
                  WHERE (user_idx = ? AND posting_idx = ?)`;

    mysql.query(sql, [userIdx, postingIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 1) {
          resolve(rows);
        } else {
          reject(52400);
        }
      }
    });
  });
};

/*******************
 *  Show Bookmark
 *  @param: useridx, postingIdx
 ********************/
exports.showBookmark = (userIdx) => {
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM posting_bookmark
                  WHERE user_idx = ?`;

    mysql.query(sql, userIdx, (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length !== 0) {
          resolve(rows);
        } else {
          reject(53400);
        }
      }
    });
  });
};
