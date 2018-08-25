const validator = require('validator');

const authModel = require('../models/AuthModel');
const errorCode = require('../utils/error').code;

let tokenError = {
  name:'tokenError',
  errors:{}
};

/*******************
 *  Authenticate
 ********************/
exports.auth = (req, res, next) => {
  if (!req.headers.token) {
    tokenError.errors = { message : 'Access Token is required' };
    return res.status(errorCode[10400].status)
              .json(errorCode[10400].contents);
  } else {
    authModel.auth(req.headers.token, (err, userData) => {
      if (err) {
        console.log(err);
        return res.status(errorCode[err].status)
                  .json(errorCode[err].contents);
      } else {
        req.userData = userData;
        return next();
      }
    });
  }
};