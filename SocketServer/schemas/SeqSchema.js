const mongoose = require('mongoose');

let Schema = {};

Schema.createSchema = (mongoose) => {
  const seqSchema = mongoose.Schema({
    _id: { type: String },
    idx: { type: Number }
  });

  return seqSchema;
};

module.exports = Schema;