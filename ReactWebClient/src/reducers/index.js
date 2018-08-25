import { createStore, combineReducers } from 'redux'
import { reducer as formReducer } from 'redux-form'

import AppReducer from './AppReducer';
import GeoMsgReducer from './messages/GeoMsgReducer';
import DirectMsgReducer from './messages/DirectMsgReducer';
import UserReducer from './user/UserReducer';

const rootReducer = combineReducers({
  app: AppReducer,
  form: formReducer,
  user: UserReducer,
  main: GeoMsgReducer,
  direct: DirectMsgReducer
});

export default rootReducer;
