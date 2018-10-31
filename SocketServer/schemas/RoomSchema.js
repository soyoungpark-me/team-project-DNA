const mongoose = require('mongoose');

const paginationCount = require('../utils/config').pagination_count;
const helpers = require('../utils/helpers');
const dmSchema = global.utils.mongo.dmSchema.obj;

let Schema = {};

Schema.createSchema = (mongoose) => {
  const roomSchema = mongoose.Schema({
    idx: { type: Number, index: { unique: true } },
    users : [{
      idx: { type: Number, required: true },
      nickname: { type: String, required: true },
      avatar: String
    }],
    blind: [ String ],
    enable: { type: Number, required: true, default: 1 },
    messages: [ dmSchema ],
    last_message: String,
    last_type: String,
    created_at : { type : Date, index: { unique : false }, default: Date.now },
    updated_at : { type : Date, index: { unique : false }, default: Date.now }
  });


  /*******************
   * 메소드 시작
  ********************/

  roomSchema.pre('save', function(next) {
    let doc = this;
    
    global.utils.mongo.seqModel.findByIdAndUpdate(
      {_id: "room"}, {$inc: {idx: 1}}, {upsert: true, new: true}, function(err, count) {

      if (err) {
        console.log(err);
        return next(err);
      }
      
      doc.idx = count.idx;
      next();
    });
  });

  // search : 해당 채팅방이 이미 존재하는지 확인
  roomSchema.static('search', function(Idxuser1, Idxuser2, callback) {
    return this.find({      
      $and: [ 
        { users: { $elemMatch: { idx: Idxuser1 }}}, 
        { users: { $elemMatch: { idx: Idxuser2 }}}
      ]
    }, callback);
  });

  // selectOne : 하나 조회하기
  roomSchema.static('selectOne', function(idx, callback) {
    return this.find({ idx: parseInt(idx) }, callback);
  });

  // selectAll : 전체 조회하기
  roomSchema.static('selectAll', function(userIdx, page, callback) {
    if (!page) { // 페이지 인자가 없음 : 페이지네이션이 되지 않은 경우
      return this.find({enable: 1, users: { $elemMatch: { idx: userIdx }}}, {'messages': 0}, callback)
        .sort('-updated_at')
    } else {     // 페이지 인자가 있음 : 페이지네이션 적용
      return this.find({enable: 1, users: { $elemMatch: { idx: userIdx }}}, {'messages': 0}, callback)
        .sort('-updated_at')
        .skip((page-1) * paginationCount).limit(paginationCount);
    }    
  });

  // close : 방 나가기 기능
  roomSchema.static('close', function(userIdx, roomIdx, callback) {
    this.findOne({ idx: parseInt(roomIdx) }, (err, room) => {
      if (err) {
        const customErr = new Error("Error occurred while Selecting Room");
        return customErr;
      }
      if (!room) {
        const customErr = new Error("Room with this Idx does not exist");
        return callback(customErr);
      }
      this.find({ $and: [{ idx: room.idx }, { blind: userIdx }] }, (err, blind) => {
        if (err) {
          const customErr = new Error("Error occurred while Selecting Room's blind list");
          return callback(customErr);
        }        
        if (blind.length > 0) {
          const customErr = new Error("This Idx is already added to the Room's blind list");
          return callback(customErr);
        }
        // Room도 존재하고 해당 유저의 idx도 존재할 경우
        this.findOneAndUpdate(
          { idx: parseInt(roomIdx) },
          { $push: { blind: userIdx } },
          callback
        );
      });
    });
  });

  // toogleAble : 방 활성화/비활성화 기능
  roomSchema.static('toogleAble', function(roomIdx, enable, callback) {
    this.findOneAndUpdate({ idx: parseInt(roomIdx) },
      { $set: { enable: enable }}, { new: true }, callback);
  });

  // saveDM : DM 저장하기
  roomSchema.static('saveDM', function(roomIdx, dmData, callback) {
    this.findOneAndUpdate(
      { idx: parseInt(roomIdx) },
      { $push: { messages: dmData } },
      callback
    );
  });

  // updated : updated_at 현재 시간으로 변경하기
  roomSchema.static('updated', function(roomIdx, data, callback) {
    this.findOneAndUpdate(
      { idx: parseInt(roomIdx) },
      { "$set": { updated_at: helpers.getCurrentDate(), last_message: data.contents, last_type: data.type }},
      callback
    );
  });

  return roomSchema;
};

module.exports = Schema;