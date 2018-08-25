import React, { Component } from 'react';
import Notification from 'react-web-notification';
import { ToastContainer, Slide } from "react-toastify";
import { BrowserRouter, Route, Link } from 'react-router-dom';

/* for Redux */
import { connect } from 'react-redux';
import reducers from './../reducers';

import { setSocketConnected, setGeoPosition } from './../actions/AppActions';

/* import Components */
import BeforeLoginLayout from './layout/BeforeLoginLayout';
import AfterLoginLayout from './layout/AfterLoginLayout';

import jQuery from "jquery";
window.$ = window.jQuery = jQuery;

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      data: null
    }
  }

  // 앱이 시작될 때 Fetch 해오기 시작
  componentWillMount() {
    this.props.setSocketConnected();
    this.props.setGeoPosition();
  }

  render() {
    let renderLayout;

    if (localStorage.getItem('token')) {
      renderLayout = <AfterLoginLayout />;
    } else {
      renderLayout = <BeforeLoginLayout />;
    }

    return (
      <BrowserRouter>
        <div className="h100">
        { renderLayout }
        <ToastContainer transition={Slide} position="top-right" rtl={false}
          autoClose={2000} hideProgressBar newestOnTop closeOnClick
          pauseOnVisibilityChange draggable={false} pauseOnHover />
        </div>
      </BrowserRouter>
    );
  }
}

export default connect(null,
  { setSocketConnected, setGeoPosition })(App);
