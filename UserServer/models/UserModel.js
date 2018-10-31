const mysql = global.utils.mysql;
const redis = global.utils.redis;
const helpers = require('../utils/helpers');

const jwt = require('jsonwebtoken');



/*******************
 *  Register
 *  @param: userData = { id, password, nickname, email, avatar, description }
 ********************/
exports.register = (userData) => {
  // 1. 아이디 중복 체크하기
  return new Promise((resolve, reject) => {    
    const sql = `SELECT id 
                   FROM users 
                  WHERE id = ?`;

    mysql.query(sql, [userData.id], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length !== 0) {
          reject(21400);
        } else {
          resolve(null);
        }
      }
    });
  })
  .then(() => {
    // 2. 메일 중복 체크하기
    return new Promise((resolve, reject) => {    
      const sql = `SELECT email 
                    FROM users 
                    WHERE email = ?`;

      mysql.query(sql, [userData.email], (err, rows) => {
        if (err) {
          reject(err);
        } else {
          if (rows.length !== 0) {
            reject(22400);
          } else {
            resolve(null);
          }
        }
      });
    })
  })
  .then(() => {
    // 2. DB에 정보 삽입하기
    return new Promise((resolve, reject) => {
      const sql = `INSERT INTO users (id, password, nickname, email, avatar, salt) 
                          VALUES     (?, ?, ?, ?, ?, ?)`;
      mysql.query(sql, [userData.id, userData.password, userData.nickname, 
        userData.email, userData.avatar, userData.salt], (err, rows) => {
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
  })
  .then((result) => {
    // 3. 결과 조회해 돌려주기
    return new Promise((resolve, reject) => {
      const sql = `SELECT idx, id 
                     FROM users 
                    WHERE idx = ?`;
      
      mysql.query(sql, result.insertId, (err, rows) => {
        if (err) {
          reject(err);
        } else {
          resolve(rows);
        }
      });
    });
  });
};


/*******************
 *  Login
 *  @param: userData = { id, password }
 *  TODO refresh token
 ********************/
exports.login = (userData) => {
  // 1. 아이디 체크
  return new Promise((resolve, reject) => {
    const sql = `SELECT id
                   FROM users
                  WHERE id = ?`;
    
    mysql.query(sql, [userData.id], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) { // 해당 아이디 없음
          reject(23400);
        } else {
          resolve(null);
        }
      }
    });
  })
  .then(() => {
    // 2. 비밀번호 체크
    return new Promise((resolve, reject) => {
      const sql = `SELECT idx, id, nickname, avatar, description, radius, is_anonymity
                     FROM users
                    WHERE id = ? AND password = ?`;

      mysql.query(sql, [userData.id, userData.password], (err, rows) => {
        if (err) {
          reject(err);
        } else {
          if (rows.length === 0) { // 비밀번호가 틀렸을 경우
            reject(24400);
          } else {
            const session = {
              idx: rows[0].idx,
              id: rows[0].id,
              nickname: rows[0].nickname,
              avatar: rows[0].avatar
            };

            const profile = {
              idx: rows[0].idx,
              id: rows[0].id,
              nickname: rows[0].nickname,
              avatar: rows[0].avatar,
              description: rows[0].description,
              radius: rows[0].radius,
              is_anonymity: rows[0].is_anonymity
            }

            const result = { session, profile }

            resolve(result);
          }
        }
      });
    });
  })
  .then((result) => {
    // 3. 토큰 발급 및 저장
    return new Promise((resolve, reject) => {
      const token = {
        accessToken: jwt.sign(result.session, process.env.JWT_CERT, {'expiresIn': "12h"}),
        refreshToken: jwt.sign(result.session, process.env.JWT_CERT, {'expiresIn': "7 days"})
      };

      // 7일 후 날짜 구하기
      const expiresIn = helpers.getAfterDate(); // 7일 후 삭제될 날짜
      redis.hmset('refreshTokens', token.refreshToken, 
        JSON.stringify({ idx: result.session.idx, id: result.session.id, expiresIn })); // 저장
      redis.hgetall('refreshTokens', (err, object) => {
        if (err){
          reject(26500);
        } else { // refresh 토큰까지 완벽하게 저장된 경우
          const response = {
            profile: result.profile,
            token
          };
      
          resolve(response);
        }
      });
    });    
  })
};            
        


/****************
 *  salt 조회
 *  @param: userData = { id }
 *  @returns {Promise<any>}
 */
exports.getSalt = (userData) => {
  return new Promise((resolve, reject) => {
    const sql = `SELECT salt 
                   FROM users 
                  WHERE id = ?`;

    mysql.query(sql, [userData], (err, rows) => {
      if (err){
        reject(err);
      } else {
        if (rows.length === 0) { // 해당 아이디 없음
          reject(23400);
        } else {
          resolve(rows[0]);
        }
      }
    });
  });
};


/*******************
 *  Select
 *  @param: idx
 ********************/
exports.select = (idx) => {
  // 1. 아이디 체크
  return new Promise((resolve, reject) => {
    const sql = `SELECT *
                   FROM users
                  WHERE idx = ?`;
    mysql.query(sql, [idx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) { // 해당 인덱스의 유저 없음
          reject(20400);
        } else {
          resolve(rows[0]);
        }
      }
    });
  });
};


/*******************
 *  PasswordCheck
 *  @param: userData = { idx, password }
 ********************/
exports.passwordCheck = (userData) => {
  return new Promise((resolve, reject) => {
    const sql = `SELECT COUNT(*)
                   FROM users
                  WHERE id = ? AND password = ?`;

    mysql.query(sql, [userData.id, userData.password], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) { // 비밀번호가 틀렸을 경우
          reject(27400);
        } else {
          resolve(true);
        }
      }
    });
  });
};



/*******************
 *  Update
 *  @param: updateData = { idx, nickname, avatar, description, 
 *            encodedPassword = { password, newSalt }
 *          }, 
 *          changePassword
 ********************/
exports.update = (updateData, changePassword) => {
  return new Promise((resolve, reject) => {
    let sql = '';
    let params = [updateData.nickname, updateData.avatar, 
                  updateData.description, updateData.idx];

    if (changePassword) {
      sql = `UPDATE users
                SET password = ?, salt = ?, nickname = ?, 
                    avatar = ?, description = ? 
              WHERE idx = ?`
      params.unshift(updateData.encodedPassword.newSalt)
      params.unshift(updateData.encodedPassword.password);
    } else {
      sql = `UPDATE users
                SET nickname = ?, avatar = ?, description = ? 
              WHERE idx = ?`
    }    

    mysql.query(sql, params, 
        (err, rows) => {
          if (err) {
            reject(err);
          } else {;
            resolve(rows);
          }
    });
  });
}



/*******************
 *  Block
 *  @param: useridx, blockUserIdx
 ********************/
exports.block = (userIdx, blockUserIdx) => {
  // 1. 해당 row가 존재하는지 먼저 확인
  return new Promise((resolve, reject) => {
    sql = `SELECT COUNT(*) 
             FROM blocks
            WHERE user_idx = ? AND block_idx = ?`;

    mysql.query(sql, [userIdx, blockUserIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        let cancelBlock = false;
        if (rows[0]['COUNT(*)'] > 0) { // 이미 존재한다 > 삭제하자!
          cancelBlock = true;
        }
        resolve(cancelBlock);
      }        
    });
  })
  .then((cancelBlock) => {  
    return new Promise((resolve, reject) => {
      let sql = '';

      if (cancelBlock) { // 차단 취소 (삭제) 하기        
        sql = `DELETE FROM blocks
                WHERE user_idx = ? AND block_idx = ?`;
      } else {           // 차단 추가 (생성) 하기
        sql = `INSERT INTO blocks (user_idx, block_idx)
                    VALUES        (?, ?)`;
      }

      mysql.query(sql, [userIdx, blockUserIdx], (err, rows) => {
        if (err) {
          reject(err);
        } else {
          if(rows.affectedRows === 1) {      
            const result = {cancelBlock, rows};
            resolve(result);
          }
        }
      });
    })
  })
  .then((result) => {
    // 3. 결과 조회해 돌려주기
    return new Promise((resolve, reject) => {
      if (!result.cancelBlock) { // 생성되었을 때는 생성한 row 리턴
        const sql = `SELECT user_idx, block_idx, created_at
                      FROM blocks 
                      WHERE idx = ?`;
        
        mysql.query(sql, result.rows.insertId, (err, rows) => {
          if (err) {
            reject(err);
          } else {
            resolve(rows);
          }
        });
      } else {
        resolve(0);
      }
    });
  });
}



/*******************
 *  SelectBlock
 *  @param: idx
 ********************/
exports.selectBlock = (idx) => {
  return new Promise((resolve, reject) => {
    sql = `SELECT idx, user_idx, block_idx, created_at
             FROM blocks
            WHERE user_idx = ?`
    
    mysql.query(sql, [idx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows);
      }
    })
  });
};



/*******************
 *  addPoint
 *  @param
 ********************/
exports.addPoints = () => {
  return new Promise((resolve, reject) => {
    sql = `UPDATE users
              SET points = points + 100
            WHERE is_faulty = 0`;
    mysql.query(sql, [], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows.affectedRows);
      }
    });
  });
}



/*******************
 *  Report
 *  @param: useridx, reportUserIdx
 ********************/
exports.report = (userIdx, reportUserIdx) => {
  // 1. 해당 row가 존재하는지 먼저 확인
  return new Promise((resolve, reject) => {
    sql = `SELECT idx 
             FROM reports
            WHERE user_idx = ? AND report_idx = ?`;
            
    mysql.query(sql, [userIdx, reportUserIdx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        if (rows.length === 0) { // 존재하지 않을 경우에만 신고
          resolve();
        } else {
          reject(28400);
        }
      }        
    });
  })
  .then(() => {  
    return new Promise((resolve, reject) => {
      sql = `INSERT INTO reports (user_idx, report_idx)
                      VALUES (?, ?)`;

      mysql.query(sql, [userIdx, reportUserIdx], (err, rows) => {
        if (err) {
          reject(29400);
        } else {
          if(rows.affectedRows === 1) {      
            resolve(rows);
          }
        }
      });      
    })
  })
  .then((result) => {
    // 3. 결과 조회해 돌려주기
    return new Promise((resolve, reject) => {
      const sql = `SELECT user_idx, report_idx, created_at
                    FROM reports 
                    WHERE idx = ?`;
      
      mysql.query(sql, result.insertId, (err, rows) => {
        if (err) {
          reject(err);
        } else {
          resolve(rows);
        }
      });
    });
  });
}


/*******************
 *  SelectPoints
 *  @param: idx
 ********************/
exports.selectPoints = (idx) => {
  return new Promise((resolve, reject) => {
    sql = `SELECT points
             FROM users
            WHERE idx = ?`;
            
    mysql.query(sql, [idx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows[0].points);
      }
    })
  });
}



/*******************
 *  reducePoints
 *  @param: idx
 ********************/
exports.reducePoints = (idx) => {
  return new Promise((resolve, reject) => {
    sql = `UPDATE users
              SET points = points - 100
            WHERE idx = ?`;
            
    mysql.query(sql, [idx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows);
      }
    })
  });
}