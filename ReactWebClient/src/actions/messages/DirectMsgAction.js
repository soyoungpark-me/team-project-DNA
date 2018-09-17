import axios from 'axios';

import config from './../../config.js';

export const GET_CONVERSATIONS = "GET_CONVERSATIONS";
export const GET_MESSAGES = "GET_MESSAGES";
export const SEND_MESSAGE = "SEND_MESSAGE";
export const SET_FRIENDS_LIST = "SET_FRIENDS_LIST";

const ROOT_URL = `${config.SERVER_HOST}:${config.SOCKET_PORT}/api`;
let token = '';
if (sessionStorage.getItem("token")) {
  token = JSON.parse(sessionStorage.getItem("token")).accessToken;
}

export function getConversations(page) {
  const request = axios.get(`${ROOT_URL}/rooms/${page}`,
  { headers: { "token": token} });

  return {
    type: GET_CONVERSATIONS,
    payload: request
  }
}

export function getMessages(idx, page){
  const request = axios.get(`${ROOT_URL}/room/${idx}/messages/${page}`,
    { headers: { "token": token } });

  return {
    type: GET_MESSAGES,
    payload: request
  }
}

export function sendMessage(values, type, conversationIdx) {
  return (dispatch, getState) => {
    const state = getState();

    const messageData = {
      room_idx: conversationIdx,
      type,
      contents: values.contents
    };

    // axios로 직접 통신하지 않고 app에 직접 연결된 socket을 통해 send_message 이벤트를 발생시킨다.
    state.app.socket.emit("save_dm", token, messageData);

    dispatch({
      type: SEND_MESSAGE
    });
  }
}
