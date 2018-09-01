exports.code =
{
  // Auth Api 관련 에러 코드
  // auth
  10400 : {
    status: 400,
    contents: {
      code: 11400,
      message: "Access or Refresh Token is required"
    }
  },
  11400 : {
    status: 400,
    contents: {
      code: 11400,
      message: "Token is expired"
    }
  },
  12400 : {
    status: 400,
    contents: {
      code: 12400,
      message: "Token is invalid"
    }
  },

  // refresh 
  13400 : {
    status: 400,
    contents: {
      code: 13400,
      message: "Need to Login Again. Refresh Token is expired or invalid"
    }
  },


  // User Api 관련 에러 코드
  // select
  20400 : {
    status: 400,
    contents: {
      code: 20400,
      message: "User with this Idx does not exist"
    }
  },

  // register
  21400 : {
    status: 400,
    contents: {
      code: 21400,
      message: "This ID already exists"
    }
  },
  22400 : {
    status: 400,
    contents: {
      code: 22400,
      message: "This Email already exists"
    }
  },
  22500 : {
    status: 500,
    contents: {
      code: 22500,
      message: "Error occurred while saving the user data into DB"
    }
  },

  // login
  23400 : {
    status: 400,
    contents: {
      code: 23400,
      message: "This ID does not exist"
    }
  },
  24400 : {
    status: 400,
    contents: {
      code: 24400,
      message: "Wrong Password"
    }
  },
  25400 : {
    status: 400,
    contents: {
      code: 25400,
      message: "User with this ID does not exist"
    }
  },
  26500: {
    status: 500,
    contents: {
      code: 26500,
      message: "Error occurred while saving the token into Redis"
    }
  },

  // update
  27400: {
    status: 400,
    contents: {
      code: 27400,
      message: "Passwords do not match"
    }
  },

  // report
  28400: {
    status: 400,
    contents: {
      code: 28400,
      message: "This user is already reported"
    }
  },
  29400: {
    status: 400,
    contents: {
      code: 29400,
      message: "No such user exists (Foreign Key Error)"
    }
  }
};