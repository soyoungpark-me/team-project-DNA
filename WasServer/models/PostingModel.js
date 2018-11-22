const mysql = global.utils.mysql;
const redis = global.utils.redis;

/*******************
 *  Write
 *  @param: useridx, userLoc, date, pcontents
 ********************/
exports.write = (userIdx, userNick, userAvatar, longitude, latitude, date, title, contents, onlyme) => {
  return new Promise((resolve, reject) => {
    const sql = `INSERT INTO posting (writer_idx, nickname, avatar, longitude, latitude, posting_date, title, contents, onlyme)
                        VALUES     (?, ?, ? , ?, ?, ?, ?, ?, ?)`;

    mysql.query(sql, [userIdx, userNick, userAvatar, longitude, latitude, date, title, contents, onlyme], (err, rows) => {
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
 *  Show Allpostings
 *  @param: postingidx
 ********************/
exports.showAll = (userIdx) => {

  return new Promise((resolve, reject) => {
    const sql = `SELECT posting_idx
                  FROM posting
                  WHERE writer_idx = ? OR onlyme = false`;

    mysql.query(sql, userIdx, (err, rows) => {
      if (err) {
        reject(err);
      } else {
        let comments = [];
        if (rows.length === 0) {
          reject(44400);
        } else {
          resolve(rows);
        }
      }
    });
  });
};

/*******************
 *  Show posting(one)
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
          const pContents = {
            posting_idx: rows[0].posting_idx,
            writer_idx: rows[0].writer_idx,
            posting_date: rows[0].posting_date,
            title: rows[0].title,
            contents: rows[0].contents,
            likes_cnt: rows[0].likes_cnt,
            latitude: rows[0].latitude,
            longitude: rows[0].longitude,
            onlyme: rows[0].onlyme,
            nickname: rows[0].nickname,
            avatar: rows[0].avatar
          };

          // const result = pContents
          resolve(pContents);
        }
      }
    });
  })
  .then((pContents) => {
    return new Promise((resolve, reject) => {
      const sql = `SELECT *
                    FROM posting_reply
                    WHERE posting_idx = ?`;

      mysql.query(sql, postingIdx, (err, rows) => {
        if(err){
          reject(err);
        } else {
          if(rows.length !== 0){
            let pReply = [];

            for(var i = 0; i<rows.length; i++){
              pReply[i] = {
                user_idx: rows[i].user_idx,
                reply_idx: rows[i].reply_idx,
                reply_contents: rows[i].reply_contents,
                nickname: rows[i].nickname,
                avatar: rows[i].avatar,
                date: rows[i].date
              };

              const result2 = {pContents, pReply}

              resolve(result2);
            }
          }
          else{
            resolve(pContents);
          }
        }
      });
    });
  });
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
          reject(46400);        // 이미 공감중입니다
        } else {
          resolve(true);      // 공감을아직안함. 밑으로 ㄱㄱ
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
          if (rows.affectedRows === 1) {
            resolve(rows);
          } else {
            reject(47400);
          }
        }
      });
    })
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
  // 1. 공감 리스트에서 삭제하기
  return new Promise((resolve, reject) => {
    const sql = `DELETE FROM posting_likes
                  WHERE (user_idx = ? AND posting_idx = ?)`;

    mysql.query(sql, [userIdx, postingIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.affectedRows === 1) {
          resolve(rows);
        } else {
          reject(47400);
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
exports.reply = (userIdx, userNick, userAvatar, rdate, postingIdx, rcontents) => {
  return new Promise((resolve, reject) => {
    const sql = `INSERT INTO posting_reply (posting_idx, date, user_idx, nickname, avatar, reply_contents)
                        VALUES     (?, ?, ?, ?, ?, ?)`;

    mysql.query(sql, [postingIdx, rdate, userIdx, userNick, userAvatar, rcontents], (err, rows) => {
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
    const sql = `SELECT * FROM posting_bookmark
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
                  FROM posting, posting_bookmark
                  WHERE posting_bookmark.user_idx = ? AND posting.posting_idx = posting_bookmark.posting_idx`;

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

/*******************
 *  Show my posts
 *  @param: useridx
 ********************/
exports.showMyPost = (userIdx) => {
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                  FROM posting
                  WHERE writer_idx = ?`;

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
