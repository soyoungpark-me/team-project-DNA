const mysql = global.utils.mysql;
const redis = global.utils.redis;


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