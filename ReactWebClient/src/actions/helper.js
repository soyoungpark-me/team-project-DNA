import axios from 'axios';

import config from './../config.js';

const USER_API_URL = `${config.SERVER_HOST}:${config.USER_PORT}/api`;
const SOCKET_API_URL = `${config.SOCKET_HOST}:${config.SOCKET_PORT}/api`;
const AWS_LAMBDA_API_URL = "https://ynqleoac2k.execute-api.ap-northeast-2.amazonaws.com/dev/upload";

let token;

if (sessionStorage.getItem('token')) {
  token = JSON.parse(sessionStorage.getItem('token')).accessToken;
}

export async function fetchOtherProfile(idx) {
  let result = '';

  await axios.get(`${USER_API_URL}/user/${idx}`,
    {headers: { 'token' : token }})
    .then((response) => {result = response});

  return result;
};

export async function imageFileUpload(formData, type) {
  let result = '';
  
  await axios.post(`${AWS_LAMBDA_API_URL}?type=${type}`, formData,
    { headers: { 'Content-Type': 'multipart/form-data' }})
    .then((response) => {result = response});

  return result;
};