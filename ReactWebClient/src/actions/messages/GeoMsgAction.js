import axios from 'axios';

import config from './../../config.js';

export const GET_MESSAGES = "GET_MESSAGES";
export const GET_BEST_MESSAGES = "GET_BEST_MESSAGES";
export const SEND_MESSAGE = "SEND_MESSAGE";
export const SET_USER_LIST = "SET_USER_LIST";
export const APPLY_LIKE = "APPLY_LIKE";

const ROOT_URL = `${config.SERVER_HOST}:${config.SOCKET_PORT}/api`;
let token = '';

export function getMessages(coords, radius, page){
  if (localStorage.getItem("token")) {
    token = JSON.parse(localStorage.getItem("token")).accessToken;
  }

  const request = axios.post(`${ROOT_URL}/messages/${page}`,
    { lng: coords.lng, lat: coords.lat, radius },
    { headers: { "token": token } });

  return {
    type: GET_MESSAGES,
    payload: request
  }
}

export function getBestMessages(coords, radius){
  if (localStorage.getItem("token")) {
    token = JSON.parse(localStorage.getItem("token")).accessToken;
  }

  const request = axios.post(`${ROOT_URL}/best`,
    { lng: coords.lng, lat: coords.lat, radius },
    { headers: { "token": token } });

  return {
    type: GET_BEST_MESSAGES,
    payload: request
  }
}

export function sendMessage(values, type) {
  return (dispatch, getState) => {
    const state = getState();
    const radius = state.user.profile.radius;

    const messageData = {
      lng: state.app.position.lng,
      lat: state.app.position.lat,
      contents: values.contents,
      type
    };

    // axios로 직접 통신하지 않고 app에 직접 연결된 socket을 통해 send_message 이벤트를 발생시킨다.
    state.app.socket.emit("save_msg", token, messageData, radius);

    dispatch({
      type: SEND_MESSAGE
    });
  }
}

export function applyLike(idx) {
  return (dispatch, getState) => {
    const state = getState();

    // axios로 직접 통신하지 않고 app에 직접 연결된 socket을 통해 send_message 이벤트를 발생시킨다.
    state.app.socket.emit("like", token, idx);

    dispatch({
      type: APPLY_LIKE
    });
  }
}

export function setUserList(value) {
  return {
    type: SET_USER_LIST,
    payload: value
  }
}
