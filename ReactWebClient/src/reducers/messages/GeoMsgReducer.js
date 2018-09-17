import { GET_MESSAGES, SET_USER_LIST, GET_BEST_MESSAGES } from './../../actions/messages/GeoMsgAction';

import { FETCH_DATA_SUCCESS, FETCH_BEST_SUCCESS } from './../../actions/index';
// import checkError from './../checkError';

const INITIAL_STATE = {
  messages: null,
  best: null,
  users: null
}

export default function(state = INITIAL_STATE, action){
  // checkError(action);

  switch(action.type) {
    case FETCH_DATA_SUCCESS:
      return { ...state, messages: action.payload.result };

    case FETCH_BEST_SUCCESS:
      return { ...state, best: action.payload.result };

    case GET_MESSAGES:
      return { ...state, messages: action.payload.result };

    case GET_BEST_MESSAGES:
      if (action.payload.data && action.payload.data.result) {
        return { ...state, best: action.payload.data.result };
      } else {
        return { ...state, best: null };
      }      

    case SET_USER_LIST:
      return {...state, users: action.payload }

    default:
      return state;
  }
}
