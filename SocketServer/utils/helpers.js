const fs = require('fs');

const AWS = require('aws-sdk');
const multer = require('multer');
const multerS3 = require('multer-s3');

const authModel = require('../models/AuthModel');

/* Image Upload */
AWS.config.accessKeyId = process.env.AWS_ACCESS_KEY;
AWS.config.secretAccessKey = process.env.AWS_SECRET_KEY;
AWS.config.region = process.env.AWS_REGION;

const S3 = new AWS.S3();

const upload = multer({
  storage: multerS3({
    s3: S3,
    bucket: 'dna-edge/images',
    acl: 'public-read',
    key: function (req, file, callback) {
      const fname = Date.now() + '_' + file.originalname;
      callback(null, fname);
    }
  })
});

exports.upload = upload;
// exports.uploadArray = multer({storage: storageS3}).array('image', 5);


exports.getClientId = (customId) => {
  let result = -1;

  for(let i=0; i<clients.length; i++) {
    if(clients[i].customId === customId) {
      result = clients[i].clientId;
      break;
    }
  }

  return result;
}

exports.returnAuth = (token) => {
  return new Promise((resolve, reject) => {
    authModel.auth(token, (err, userData) => {
      if (err) {
        reject(err);
      } else {
        resolve(userData);
      }
    });
  });
}

exports.getCurrentDate = () => {
  var date = new Date();
 
  var year = date.getFullYear();
  var month = date.getMonth();
  var today = date.getDate();
  var hours = date.getHours();
  var minutes = date.getMinutes();
  var seconds = date.getSeconds();
  var milliseconds = date.getMilliseconds();
 
  return new Date(Date.UTC(year, month, today, hours, minutes, seconds, milliseconds));
}