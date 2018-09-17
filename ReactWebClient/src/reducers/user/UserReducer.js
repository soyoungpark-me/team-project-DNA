import { GET_PROFILE, SET_USER_INDEX } from './../../actions/user/UserAction';
// import checkError from './../checkError';

const INITIAL_STATE = {
  profile: null,
  index: null,
  refresh: null
}

export default function(state = INITIAL_STATE, action) {
  // checkError(action);

  switch(action.type) {
    case GET_PROFILE:
      if (action.payload && action.payload.data)
        return { ...state, profile: action.payload.data.result }
      else
        return { ...state, profile: null }  

    case SET_USER_INDEX:
      return { ...state, index: action.payload }

    default:
      return state;
  }
}
