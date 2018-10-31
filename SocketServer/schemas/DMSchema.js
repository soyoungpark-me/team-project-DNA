const mongoose = require('mongoose');

let Schema = {};

Schema.createSchema = (mongoose) => {
  const dmSchema = mongoose.Schema({
    sender_idx: { type: Number, required: true},
    contents: { type: String, required: true },
    type: { type: String, default: "Message" },
    created_at: { type: Date, default: Date.now }
  });

  return dmSchema;
};

module.exports = Schema;