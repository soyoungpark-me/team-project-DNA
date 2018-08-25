const mysql = global.utils.mysql;

/*******************
 *  SelectBlock
 *  @param: idx
 ********************/
exports.selectBlock = (idx) => {
  return new Promise((resolve, reject) => {
    sql = `SELECT block_idx
             FROM blocks
            WHERE user_idx = ?`
    
    mysql.query(sql, [idx], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        result = [];
        for (let i = 0; i<rows.length; i++) {          
          result.push(rows[i].block_idx);
        }
        resolve(result);
      }
    })
  });
};


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