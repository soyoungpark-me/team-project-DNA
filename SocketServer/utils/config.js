/******************************************************************************
' 파일     : config.js
' 작성     : 박소영
' 목적     : mongoDB 스키마와 모델, 페이지네이션 단위 등 서버의 변수들을 변경할 수 있는 파일입니다.
******************************************************************************/

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
    },
    {
      file: '../schemas/SeqSchema',
      collection: 'seq',
      schemaName: 'seqSchema',
      modelName: 'seqModel'
    }
  ],
  pagination_count: 20,
  ping_interval: 2000,
  ping_timeout: 5000
}
