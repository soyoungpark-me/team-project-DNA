const mongoose = require('mongoose');

const paginationCount = require('../utils/config').pagination_count;

let Schema = {};

Schema.createSchema = (mongoose) => {
  const testSchema = mongoose.Schema({
    idx: { type: Number },
    user: {
      idx: { type: Number, required: true }
    },
    position: {
      type: { type: String, default: "Point"},
      coordinates: [{ type: Number }]
    },
    contents: { type: String, required: true },
    type: { type: String, default: "Message" },
    like_count: { type: Number, default: 0, index: true },
    likes: [ Number ],
    created_at : { type : Date, index: { unique : false }, default: Date.now }
  });

  testSchema.index({ location: '2dsphere'});

  return testSchema;
};

module.exports = Schema;