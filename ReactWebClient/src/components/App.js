import React, { Component } from 'react';
import { ToastContainer, Slide } from "react-toastify";
import { Router, withRouter } from 'react-router-dom';
import history from './../history';

/* for Redux */
import { connect } from 'react-redux';

import { setSocketConnected, setGeoPosition } from './../actions/AppActions';
import { setUserIndex } from './../actions/user/UserAction';

/* import Components */
import BeforeLoginLayout from './layout/BeforeLoginLayout';
import AfterLoginLayout from './layout/AfterLoginLayout';

import jQuery from "jquery";
window.$ = window.jQuery = jQuery;

function mapStateToProps(state) {
  return {
    index: state.user.index
  };
}

const App = withRouter(props => <MyComponent {...props}/>);

class MyComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      data: null
    }
  }

  // 앱이 시작될 때 Fetch 해오기 시작
  componentWillMount() {
    this.props.setGeoPosition();

    const path = this.props.location.pathname;

    if (path === "/login" && sessionStorage.getItem("token")) {
      // 토큰이 있어 넘어왔음에도 login으로 라우팅을 시도할 경우
      // 홈으로 보내버립니다.
      this.props.history.push('/main');
      return;
    }
    
    if (path === '/logout') {
      // 로그아웃으로 넘어왔을 땐 토큰을 모두 삭제하고
      sessionStorage.removeItem("token");
      this.props.setUserIndex(null);
      // 홈으로 보내버립니다.
      history.push('/login');
      return;
    }
  }

  render() {
    let renderLayout;

    if (sessionStorage.getItem('token')) {
      renderLayout = <AfterLoginLayout />;
    } else {
      renderLayout = <BeforeLoginLayout />;
    }

    return (
      <Router history={history}>
        <div className="h100">
        { renderLayout }
        <ToastContainer transition={Slide} position="top-right" rtl={false}
          autoClose={2000} hideProgressBar newestOnTop closeOnClick
          pauseOnVisibilityChange draggable={false} pauseOnHover />
        </div>
      </Router>
    );
  }
}

export default connect(mapStateToProps,
  { setSocketConnected, setGeoPosition, setUserIndex })(App);
