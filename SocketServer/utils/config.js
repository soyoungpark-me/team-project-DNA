module.exports = {
  db_schemas : [
    {
      file: '../schemas/MessageSchema',
      collection: 'message',
      schemaName: 'messageSchema',
      modelName: 'messageModel'
    },
    {
      file: '../schemas/DMSchema',
      collection: 'dm',
      schemaName: 'dmSchema',
      modelName: 'dmModel'
    },
    {
      file: '../schemas/RoomSchema',
      collection: 'room',
      schemaName: 'roomSchema',
      modelName: 'roomModel'
    }
  ],
  pagination_count: 30,
  ping_interval: 2000,
  ping_timeout: 5000
}