import { GET_MESSAGES, SEND_MESSAGE, SET_USER_LIST,
  MESSAGE_MAKE_UPDATE, MESSAGE_MAKE_NOT_UPDATE }
  from './../../actions/messages/GeoMsgAction';

import { FETCH_DATA_SUCCESS } from './../../actions/index';
import checkError from './../checkError';

const INITIAL_STATE = {
  messages: null,
  users: null
}

export default function(state = INITIAL_STATE, action){
  checkError(action);

  switch(action.type) {
    case FETCH_DATA_SUCCESS:// return data and set fetching = false
      return { ...state, messages: action.payload.result };

    case GET_MESSAGES:
      return { ...state, messages: action.payload.result };

    case MESSAGE_MAKE_UPDATE:
      return { ...state, update: true}

    case MESSAGE_MAKE_NOT_UPDATE:
      return { ...state, update: false}

    case SET_USER_LIST:
      return {...state, users: action.payload }

    default:
      return state;
  }
}
