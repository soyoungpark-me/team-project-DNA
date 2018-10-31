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
  },

  // Friend API 관련 에러 코드
  // Add
  30400: {
    status: 400,
    contents: {
      code: 30400,
      message: "Already Friend"
    }
  },
  //delete
  31400: {
    status: 400,
    contents: {
      code: 31400,
      message: "Not Friend"
    }
  },
  //show
  32400: {
    status: 400,
    contents: {
      code: 32400,
      message: "No Friend"
    }
  },
  //show list
  33400: {
    status: 400,
    contents: {
      code: 33400,
      message: "No Request Exists"
    }
  },
  //add request
  34400: {
    status: 400,
    contents: {
      code: 34400,
        message: "Already sent request"
    }
  },

  // Posting API 관련 에러 코드
  // Write
  40400: {
    status: 400,
    contents: {
      code: 40400,
      message: "Write Posting Failed"
    }
  },
  //delete/alter access denied
  41400: {
    status: 400,
    contents: {
      code: 41400,
      message: "Access denied to the posting"
    }
  },
  // delete
  42400: {
    status: 400,
    contents: {
      code: 42400,
      message: "Delete failed"
    }
  },
  //delete likes
  43400: {
    status: 400,
    contents: {
      code: 43400,
      message: "No Likes on this posting"
    }
  },
  //show
  44400: {
    status: 400,
    contents: {
      code: 44400,
        message: "No posting exists"
    }
  },
  //update
  45400: {
    status: 400,
    contents: {
      code: 45400,
        message: "Update posting failed"
    }
  },
  //like
  46400: {
    status: 400,
    contents: {
      code: 46400,
        message: "Already likes posting"
    }
  },
  47400: {
    status: 400,
    contents: {
      code: 47400,
        message: "un/likes posting failed"
    }
  },
  48400: {
    status: 400,
    contents: {
      code: 48400,
        message: "change likes posting count failed"
    }
  },
  49400: {
    status: 400,
    contents: {
      code: 49400,
        message: "No likes exists"
    }
  },
  50400: {
    status: 400,
    contents: {
      code: 50400,
        message: "Write/Delete Reply Failed"
    }
  },
  51400: {
    status: 400,
    contents: {
      code: 51400,
        message: "No Reply Exists"
    }
  },
  52400: {
    status: 400,
    contents: {
      code: 52400,
        message: "Bookmark Failed"
    }
  },
  53400: {
    status: 400,
    contents: {
      code: 53400,
        message: "No Bookmark Exists"
    }
  },
};
