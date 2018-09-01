const fs = require('fs');
const list = fs.readdirSync(__dirname).filter(dir => !dir.match(/(^\.)|index/i));
const router = require('express').Router();

const helpers = require('../utils/helpers');

module.exports = (app) => {
  router.post("/upload", helpers.upload.single("image"), function (req, res, next) {
    // 이미지를 업로드한 후 위치 정보를 돌려줍니다.
    if (req && req.file) {
      res.status(201).json(req.file.location);
    } else {
      res.status(500).json({data: "image file is not uploaded"});
    }
  });

  for (let ctrl of list) {
    app.use('/api', require(`./${ctrl}`)(router));
  }
};