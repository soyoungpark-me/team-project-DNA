const fs = require('fs');
const list = fs.readdirSync(__dirname).filter(dir => !dir.match(/(^\.)|index/i));
const router = require('express').Router();

module.exports = (app) => {
  for (let ctrl of list) {
    app.use('/api', require(`./${ctrl}`)(router));
  }
};

// 1. 먼저 fs 모듈을 이용해 해당 디렉토리의 모든 파일을 가지고 온 뒤,
// 2. 파일명에 .이 붙어있거나 index가 포함되지 않은 파일들을 끌고 온다.
// 3. 이후 list안의 각 ctrl 파일들을 가져와서 각 파일마다
// 4. /api 주소 아래에 붙여준다.