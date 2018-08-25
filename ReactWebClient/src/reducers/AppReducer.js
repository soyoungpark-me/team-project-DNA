import { SET_WEB_NOTIFY_ENABLE, SET_WEB_NOTIFY_UNABLE,
          SET_GEO_POSITION, SET_SOCKET_CONNECTED }
  from '../actions/AppActions.js';

let ignore = '';

if (Notification.permission === 'granted') {
  ignore = true;
} else {
  ignore = false;
}

const initialState = {
  ignore,
  socket: null,
  position: null
};

export default function data (state = initialState, action) {
  switch (action.type) {
    case SET_SOCKET_CONNECTED:
      return { ...state, socket: action.payload };

    case SET_WEB_NOTIFY_ENABLE:
      return { ...state, ignore: false};

    case SET_WEB_NOTIFY_UNABLE:
      return { ...state, ignore: true};

    case SET_GEO_POSITION:
      const coords = action.payload.coords;
      const result = { lat: coords.latitude, lng: coords.longitude };
      return { ...state, position: result }

    default:
      return state;
  }
}
