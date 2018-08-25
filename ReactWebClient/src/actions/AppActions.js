import axios from 'axios';
import io from 'socket.io-client';

import config from './../config.js';

export const SET_GEO_POSITION = 'SET_GEO_POSITION';
export const SET_SOCKET_CONNECTED = 'SET_SOCKET_CONNECTED';
export const SET_WEB_NOTIFY_ENABLE = 'SET_WEB_NOTIFY_ENABLE';
export const SET_WEB_NOTIFY_UNABLE = 'SET_WEB_NOTIFY_UNABLE';

const USER_API_URL = `${config.SERVER_HOST}:${config.USER_PORT}/api`;
const SOCKET_API_URL = `${config.SOCKET_HOST}:${config.SOCKET_PORT}`;

let token;
let initial = true;
if (localStorage.getItem('token')) {
  token = JSON.parse(localStorage.getItem('token')).accessToken;
}

export function setSocketConnected() {
  let socket = null;
  if(token !== null && token !== undefined) {
    socket = io.connect(SOCKET_API_URL, {transports: ['websocket']}, {rejectUnauthorized: false});
  }

  return {
    type: SET_SOCKET_CONNECTED,
    payload: socket
  };
}

export function handlePermissionGranted(){
  console.log('Permission Granted');

  return {
    type: SET_WEB_NOTIFY_ENABLE,
    payload: false
  };
}
export function handlePermissionDenied(){
  console.log('Permission Denied');

  return {
    type: SET_WEB_NOTIFY_UNABLE,
    payload: true
  };
}
export function handleNotSupported(){
  console.log('Web Notification not Supported');

  return {
    type: SET_WEB_NOTIFY_UNABLE,
    payload: true
  };
}

export function setGeoPosition() {
  const geolocation = navigator.geolocation;

  const position = new Promise((resolve, reject) => {
    if (!geolocation) {
      reject(new Error('Not Supported'));
    }

    geolocation.getCurrentPosition((coords) => {
      const result = { lat: coords.coords.latitude, lng: coords.coords.longitude };

      resolve(coords);
    }, () => {
      reject (new Error('Permission denied'));
    });
  });

  return {
    type: SET_GEO_POSITION,
    payload: position
  }
};
