import React, { Component } from "react";
import Notification from 'react-web-notification';
import 'react-toastify/dist/ReactToastify.css';
import { BrowserRouter, Switch, Route, Redirect, withRouter } from 'react-router-dom';

import { connect } from 'react-redux';

import { setGeoPosition, handlePermissionGranted,
  handlePermissionDenied, handleNotSupported } from './../../actions/AppActions';
import { setUserList } from './../../actions/messages/GeoMsgAction';
import { getProfile } from './../../actions/user/UserAction';

/* import Components */
import { NavAfterComponent } from './nav/NavComponents';
import MainComponent from './../contents/main/MainComponent';
import DirectComponent from './../contents/direct/DirectComponent';

import soundMp3 from './../../../public/sounds/sound.mp3';
import soundOgg from './../../../public/sounds/sound.ogg';
import speakerPng from './../../../public/images/speaker.png';

const AfterLoginLayout = withRouter(props => <MyComponent {...props}/>);

function mapStateToProps(state) {
  return {
    ignore: state.app.ignore,
    socket: state.app.socket,
    position: state.app.position,
    profile: state.user.profile
  };
}

class MyComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      title: ''
    };

    this.makePushNoti = this.makePushNoti.bind(this);
    this.handleNotiOnShow = this.handleNotiOnShow.bind(this);
  }

  componentWillMount() {
    const path = this.props.location.pathname;

    if (path === "/login") {
      // 토큰이 있어 넘어왔음에도 login으로 라우팅을 시도할 경우
      // 홈으로 보내버립니다.
      this.props.history.push('/');
      return;
    } else if (path === '/logout') {
      // 로그아웃으로 넘어왔을 땐 토큰을 모두 삭제하고
      localStorage.removeItem("token");
      // 홈으로 보내버립니다.
      this.props.history.push('/login');
      return;
    }
  };

  componentDidUpdate() {
    // 로그인 한 후에도 프로필을 가지고 있지 않다면
    // 서버에 요청해서 다시 저장합니다.
    if (this.props.profile === null) {
      this.props.getProfile(localStorage.getItem("index"));
    }

    // 소켓 설정하기 (로그인 된 상태에서만 설정해주기 위해 AfterLoginLayout에 위치합니다)
    // 1. 현재 정보을 세팅합니다.
    const socket = this.props.socket;
    const path = this.props.location.pathname;

    if (this.props.position !== null && this.props.profile !== null) {
      const position = this.props.position;
      const profile = this.props.profile;

      let info = {
        idx     : profile.idx,                  // 현재 유저의 인덱스 값
        nickname: profile.nickname,             // 현재 유저의 별명
        avatar  : profile.avatar,               // 현재 유저의 프로필 사진 주소
        position: [position.lng, position.lat], // 클라이언트의 현재 위치
        radius  : profile.radius                // 메시지를 받아볼 반경 값
      };

      // 2. 연결하면서 현재 정보를 서버에 전송해 저장되도록 합니다.
      socket.on('connect', function() {
        socket.emit('store', info);
      });

      // 서버에서 heartbeat가 올 경우, 응답으로 현재 정보를 넘겨줍니다.
      socket.on('ping', () => {
        let type = '';

        if (path === "/") {           // 현재 path에 따라서 요구하는 정보가 달라야 합니다.
          type = "geo";               // /(전체 채팅)일 경우에는 위치 기준 주변 접속자 리스트를,
        } else if (path === "/dm"){   // /dm (다이렉트 메시지)일 경우에는 친구 접속자 리스트를 받습니다.
          type = "direct";
        }

        socket.emit('update', type, info);  // 업데이트된 정보를 소켓에 전달해 저장합니다.
      });

      // update에 대한 응답으로 현재 접속한 유저 리스트를 받아와 state에 매핑합니다.
      socket.on("geo", (data) => {
        this.props.setUserList(data);
      });

      // 확성이 이벤트를 받았을 경우, 푸시 메시지를 생성합니다.
      socket.on("speaker", (data) => {
        this.makePushNoti(data);
      });
    }
  };

  render() {
    return(
      <div className="h100">
        <NavAfterComponent />
        <BrowserRouter>
          <div className="h100calc">
            <Switch>
              <Route exact path="/" component={MainComponent} />
              <Route exact path="/dm/:idx?" component={DirectComponent} />
            </Switch>
          </div>
        </BrowserRouter>
        <Notification
          ignore={this.state.ignore && this.state.title !== ''}
          notSupported={this.props.handleNotSupported}
          onPermissionGranted={this.props.handlePermissionGranted}
          onPermissionDenied={this.props.handlePermissionDenied}
          onShow={this.handleNotiOnShow}
          timeout={5000}
          title={this.state.title}
          options={this.state.options}
          />
          <audio id='sound' preload='auto'>
            <source src={soundMp3} type='audio/mpeg' />
            <source src={soundOgg} type='audio/ogg' />
            <embed hidden='true' autostart='false' loop='false' src={soundMp3} />
          </audio>
      </div>
    );
  };

  makePushNoti(data) {
    if(this.state.ignore) {
      return;
    }

    const title = data.user.nickname + "님의 확성기";
    const body = data.contents;
    const tag = Date.now(); // 태그 값이 서로 달라야 중복으로 알림이 생깁니다.
    const icon = speakerPng;
    // const icon = 'http://localhost:3000/Notifications_button_24.png';

    // Available options
    // See https://developer.mozilla.org/en-US/docs/Web/API/Notification/Notification
    const options = {
      tag: tag,
      body: body,
      icon: icon,
      lang: 'en',
      dir: 'ltr',
      sound: soundMp3  // no browsers supported https://developer.mozilla.org/en/docs/Web/API/notification/sound#Browser_compatibility
    }
    this.setState({
      title: title,
      options: options
    });
  };

  handleNotiOnShow(e, tag){
    document.getElementById('sound').play();
    console.log(e, 'Notification shown tag:' + tag);
  };
};

export default connect(mapStateToProps,
  { setGeoPosition, setUserList, getProfile,
    handlePermissionGranted, handlePermissionDenied,
    handleNotSupported })(AfterLoginLayout);
