/* for React */
import React from 'react';
import ReactDOM from 'react-dom';
import Favicon from 'react-favicon';
import { BrowserRouter } from 'react-router-dom';

/* for Redux */
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import ReduxThunk from 'redux-thunk';
import promise from 'redux-promise';

/* for Design, etc */
import 'bootstrap/dist/css/bootstrap.css';
import faviconPath from '../public/favicon.ico';

import App from './components/App';
import reducers from './reducers';
// import registerServiceWorker from './registerServiceWorker';

const createStoreWithMiddleware = applyMiddleware(promise, ReduxThunk)(createStore);

ReactDOM.render(
  <Provider store={createStoreWithMiddleware(reducers)}>
    <div id='under-root'>
      <Favicon url={faviconPath} />
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </div>
  </Provider>, document.getElementById('root'));
