import axios from 'axios';

import config from './../../config.js';

export const GET_CONVERSATIONS = "GET_CONVERSATIONS";
export const GET_MESSAGES = "GET_MESSAGES";
export const NEW_MESSAGE = "NEW_MESSAGE";
export const SET_FRIENDS_LIST = "SET_FRIENDS_LIST";

const ROOT_URL = `${config.SERVER_HOST}:${config.SOCKET_PORT}/api`;
let token = '';
if (localStorage.getItem("token")) {
  token = JSON.parse(localStorage.getItem("token")).accessToken;
}

export function getConversations(page) {
  const request = axios.get(`${ROOT_URL}/rooms/${page}`,
  { headers: { "token": token} });

  return {
    type: GET_CONVERSATIONS,
    payload: request
  }
}

export function getMessages(coords, radius, page){
  const request = axios.post(`${ROOT_URL}/message/${page}`,
    { lng: coords.lng, lat: coords.lat, radius },
    { headers: { "token": token } });

  return {
    type: GET_MESSAGES,
    payload: request
  }
}

// export function sendMessage(values, type) {
//   return (dispatch, getState) => {
//     const state = getState();
//     const radius = state.user.profile.radius;
//
//     const messageData = {
//       lng: state.app.position.lng,
//       lat: state.app.position.lat,
//       contents: values.contents,
//       type
//     };
//
//     // axios로 직접 통신하지 않고 app에 직접 연결된 socket을 통해 send_message 이벤트를 발생시킨다.
//     state.app.socket.emit("save_msg", token, messageData, radius);
//
//     dispatch({
//       type: SEND_MESSAGE
//     });
//   }
// }
//
// export function newMessage(value) {
//   return {
//     type: NEW_MESSAGE,
//     payload: value
//   }
// }
//
// export function setUserList(value) {
//   return {
//     type: SET_USER_LIST,
//     payload: value
//   }
// }
