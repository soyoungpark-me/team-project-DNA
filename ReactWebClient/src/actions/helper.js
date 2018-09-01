import axios from 'axios';

import config from './../config.js';

const USER_API_URL = `${config.SERVER_HOST}:${config.USER_PORT}/api`;
const SOCKET_API_URL = `${config.SOCKET_HOST}:${config.SOCKET_PORT}/api`;

let token;

if (localStorage.getItem('token')) {
  token = JSON.parse(localStorage.getItem('token')).accessToken;
}

export async function fetchOtherProfile(idx) {
  let result = '';

  await axios.get(`${USER_API_URL}/user/${idx}`,
    {headers: { 'token' : token }})
    .then((response) => {result = response});

  return result;
};

export async function imageFileUpload(file) {
  let result = '';

  await axios.post(`${SOCKET_API_URL}/upload`, file,
    { headers: { 'content-type': 'multipart/form-data' }})
    .then((response) => {result = response});

  return result;
};