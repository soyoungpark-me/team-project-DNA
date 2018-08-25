const crypto = require('crypto');

/*******
 * 난수 생성 함수
 * @returns {string}
 */
const randomString = () => {
  const chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
  const stringLength = 8;
  let randomString = '';
  for (let i=0; i<stringLength; i++) {
    let rnum = Math.floor(Math.random() * chars.length);
    randomString += chars.substring(rnum,rnum+1);
  }
  return randomString;
};

/*************
 * Crypto
 *************/
exports.doCypher = (inputpass, salt) => {
  const ranString = randomString();
  const newSalt = typeof salt !== 'undefined' ? salt: ranString;

  const iterations = 100;
  const keylen = 24;

  const derivedKey = crypto.pbkdf2Sync(inputpass, newSalt, iterations, keylen, 'sha512');
  const password = Buffer(derivedKey, 'binary').toString('hex');

  const result = { password, newSalt };
  return result;
};

exports.getAfterDate = () => {
  var date = new Date();
  date = date.setDate(date.getDate() + 7);
 
  return date;
}

exports.encrypt = (text) => {
  var cipher = crypto.createCipher(process.env.JWT_CODE, process.env.JWT_KEY);
  var crypted = cipher.update(text, 'utf8', 'base64');
  crypted += cipher.final('base64');
  return crypted;
};

exports.decrypt = (text) => {
  var decipher = crypto.createDecipher(process.env.JWT_CODE, process.env.JWT_KEY);
  var dec = decipher.update(text, 'base64', 'utf8')
  dec += decipher.final('utf8');
  return dec;
};