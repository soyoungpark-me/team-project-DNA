import { GET_CONVERSATIONS, GET_MESSAGES, SET_FRIENDS_LIST } from './../../actions/messages/DirectMsgAction';

import { FETCH_DATA_SUCCESS } from './../../actions/index';
import checkError from './../checkError';

const INITIAL_STATE = {
  conversations: null,
  messages: null,
  friends: null
}

export default function(state = INITIAL_STATE, action){
  checkError(action);

  switch(action.type) {
    case FETCH_DATA_SUCCESS:// return data and set fetching = false
      return { ...state, messages: action.payload.result };

    case GET_CONVERSATIONS:
      return { ...state, conversations: action.payload.data.result };

    case GET_MESSAGES:
      return { ...state, messages: action.payload.result };

    case SET_FRIENDS_LIST:
      return {...state, users: action.payload }

    default:
      return state;
  }
}
