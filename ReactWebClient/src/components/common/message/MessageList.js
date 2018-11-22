import React, { Component } from 'react';
import { connect } from 'react-redux';
import Loader from 'react-loader-spinner'
import Moment from 'react-moment';
import FontAwesome from 'react-fontawesome';
import 'moment/locale/ko';

import { getMessages, getBestMessages } from './../../../actions/messages/GeoMsgAction';
import { setGeoPosition } from './../../../actions/AppActions';
import { fetchDataSuccess, fetchBestSuccess } from './../../../actions/index';

import Message from './Message';
import MessageForm from './MessageForm';

import styles from './styles.css';
import config from './../../../config';

import imagePath from './../../../../public/images/empty.png';
import avatar from './../../../../public/images/avatar.png';

function mapStateToProps(state) {
  return {
    socket: state.app.socket,
    position: state.app.position,
    profile: state.user.profile,
    messages: state.main.messages,
    best: state.main.best
  };
}

const CreatedAt = (props) => (
  <div className="best-chat-created-at">
    <Moment locale="ko" format="YYYY/MM/DD">{props.date}</Moment>
    <Moment locale="ko" format="A hh:mm">{props.date}</Moment>
  </div>
);

class MessageList extends Component {
  constructor(props){
    super(props);

    this.before;            // 새로 렌더링하기 전의 리스트의 높이입니다.
    this.objDiv;            // 채팅 리스트를 감싸는 div가 들어갑니다.
    this.page = 1;          // 현재 페이지입니다. (메시지 페이지네이션)
    this.initial = true;    // 처음 렌더링 되었을 때를 나타냅니다.
    this.fetching = false;  // 현재 fetch 하고 있는 중인지를 나타냅니다.

    this.state = {
      position: null,
      messages: [],
      refs: {}
    };

    this.bestChatToggle = this.bestChatToggle.bind(this);
    this.handleInterval = this.handleInterval.bind(this);
    this.handleRequestAnimationFrame = this.handleRequestAnimationFrame.bind(this);
  }

  componentWillMount() {
    this.props.getMessages(this.props.position, this.props.profile.radius, this.page);
    this.props.getBestMessages(this.props.position, this.props.profile.radius);
    
    // 1초마다 휠의 위치를 측정합니다.
    const INTERVAL = 1000;
    this.intervalID = setInterval(this.handleInterval, INTERVAL);

    // 5. 서버로부터 새 메시지 이벤트를 받았을 경우에 화면에 새로 렌더링해줍니다.
    this.props.socket.on('new_msg', (response) => {
      this.setState({messages: [response.result, ...this.state.messages]});
      let i = 0;
      let joined = {};
      this.state.messages.map((message) => {joined[message.idx] = i; i++});
      this.setState({ refs: joined });
      this.scrollToBottom();
    });

    this.props.socket.on('apply_like', (response) => {
      const target = this.state.refs[response.result.idx];
      const messages = this.state.messages;
      messages[target] = response.result;
      this.forceUpdate();
    })
  }

  componentWillUnmount() {
    // 컴포넌트를 없애기 전에 설정한 인터벌 값을 지워줍니다.
    clearInterval(this.intervalID);
    cancelAnimationFrame(this.requestID);
    this.requestID = null;
    this.intervalID = null;
  }

  componentWillReceiveProps(nextProps){
    // 현재 1페이지일 경우에는 처음 끌어오는 경우인 것이므로
    //   메시지를 담는 따로 기존 state가 존재하는 게 아니라는 의미가 됩니다.
    //   state
    if (nextProps.messages) {
      this.setState({ messages: [...this.state.messages, ...nextProps.messages]});
      let i = this.state.messages.length;
      let joined = this.state.refs;
      nextProps.messages.map((message) => {joined[message.idx] = i; i++});
      this.setState({ refs: joined });
    }
  }  

  componentDidUpdate(prevProps){
    if (this.initial) {
      window.$(".message-list-wrapper > div:first-of-type").hide();
    }
    if (this.initial && this.state.messages.length > 0) {
      this.objDiv = document.getElementsByClassName("message-list-chat-wrapper")[0];
      this.initial = false;
      this.scrollToBottom();
      this.beforeHeight = this.objDiv.scrollHeight;      
    }

    if (!this.fetching && this.state.position !== null && this.state.position <= 0) {
      if (prevProps.messages && config.PAGINATION_COUNT === prevProps.messages.length) {
          this.beforeHeight = this.objDiv.scrollHeight;
          this.page++;

          this.props.getMessages(this.props.position, this.props.profile.radius, this.page);
          
          this.fetching = true;
          window.$(".message-list-wrapper > div:first-of-type").show();
      }
    }

    if (this.fetching && this.beforeHeight !== this.objDiv.scrollHeight) {
      const newHeight = this.objDiv.scrollHeight - this.beforeHeight;
      this.objDiv.scrollTop = newHeight;
      this.fetching = false;
      window.$(".message-list-wrapper > div:first-of-type").hide();
    }
  }

  handleInterval() {
    // Interval is only used to throttle animation frame
    cancelAnimationFrame(this.requestID);
    this.requestID = requestAnimationFrame(this.handleRequestAnimationFrame);

    // 현재 시간 (시, 분)을 구합니다.
    const now = new Date();
    const minute = now.getMinutes();
    const second = now.getSeconds();

    // 만약 현재 분이 0일 경우 베스트 챗 갱신을 요청합니다.
    if (minute === 0 && second === 0) {
      this.props.getBestMessages(this.props.position, this.props.profile.radius);
    }
  }

  handleRequestAnimationFrame() {
    const newScrollPosition = this.getWindowScrollTop();

    // Update the state only when scroll position is changed
    if (newScrollPosition !== this.state.position) {
      this.setState({
        position: newScrollPosition,
      });
    }
  }

  getWindowScrollTop() {
    const item = document.getElementsByClassName("message-list-chat-wrapper");
    if (item && item.length > 0) {
      return item[0].scrollTop;
    }
    return null;
  }

  scrollToBottom(){
    if (this.objDiv) {
      this.objDiv.scrollTop = this.objDiv.scrollHeight - this.objDiv.clientHeight;
    }
  }

  bestChatToggle() {
    if (this.props.best && this.props.best.length > 1) {
      window.$(".best-chat-wrapper").animate({
        height: window.$(".best-chat-wrapper").height() == 70 ? 70 * this.props.best.length : 70
      }, 200); 
    }
  }

  renderMessages(){
    let beforeIdx = -1;
    let beforeTime = -1;
    let tempIdx, tempTime;

    const currentUser = this.props.profile.idx;

    return this.state.messages.slice(0).reverse()
      .map((message) => {
        tempIdx = beforeIdx;
        tempTime = beforeTime;
        beforeIdx = message.user.idx;
        beforeTime = message.created_at.split('T')[0];

        return (
          <Message message={message} key={"msg"+message.idx}
            sender={(currentUser === message.user.idx) ? "me" : "you"}
            idx={this.props.profile.idx}
            start={(tempIdx !== beforeIdx) ? true : false }
            dayStart={(tempTime !== beforeTime) ? true : false} />
        )
      });
  }

  renderBestMessages(){
    return this.props.best
      .map((best, i) => {
        let contents = '';

        if (best.type === "Image") contents = "[사진]";
        else if (best.type === "Location") contents = "[좌표]";
        else contents = best.contents;

        return (
          <div className="best-chat-contents-item" key={"best"+best.idx}>
            <div className="bubble-side-wrapper">
              <p className="best-chat-rank">{i+1}위</p>
              <div className="message-thumb-up i-liked-it">
                <FontAwesome className="message-thumb-up-fa" name="star" />
                <span className="message-thumb-up-count">{best.like_count}</span>
              </div>
            </div>
            <div className="user-my-profile-top">
              <div className="avatar-wrapper">
                <img className="avatar-image"
                  src={ best.user.anonymity === 0 && best.user.avatar !== null && best.user.avatar !== "null" 
                  ? best.user.avatar : avatar} />
              </div>
              <div className="user-my-profile-text">
                <p className="user-my-profile-nickname">
                { best.user.anonymity === 1
                  ? config.ADJECTIVE[best.user.idx & 100] + " " + config.ANIMAL[best.user.idx % 100] 
                  : best.user.nickname }</p>
                <span className="best-chat-contents">
                  {contents}
                </span>
              </div>
              <CreatedAt date={best.created_at} />   
            </div>   
          </div>
        )
      });
  }

  render() {
    if (!this.state.messages || this.state.messages === null) {
      return (
        <div className='message-list-wrapper'>
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          <div className="message-list-chat-wrapper" />
        </div>
      );
    } else {
      let contents;
      let bests;

      if (this.state.messages.length === 0) {
        contents = (
          <div className="message-list-empty">
            <img src={imagePath} />
            <p>이 근방에서는 아직 작성된 메시지가 없습니다</p>
          </div>
        );
      } else {
        if (this.state.messages === -1){
        contents = (<p>{}</p>);
        } else {
          contents = this.renderMessages();
        }
      }

      if (this.props.best) {
        if (this.props.best.length === 0) {
          bests = (
            <div className="best-chat-contents-wrapper">
              <span className="ti-face-sad" />
              <p className="best-chat-list-empty">근처에 아직 작성된 베스트챗이 없습니다</p>
            </div>
          );
        } else {
          bests = (
            <div className="best-chat-contents-wrapper">
              {this.renderBestMessages()}
            </div>
          )
        }
      }
      return (
        <div className="message-list-wrapper">
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          <div className="message-list-chat-wrapper">
            {contents}
          </div>
          <div className="best-chat-wrapper">
            <FontAwesome className="best-chat-fa" name="award" />
            {bests}
            <FontAwesome className="best-chat-toggle" name="angle-down" onClick={this.bestChatToggle} />
          </div>
          <MessageForm type={ this.props.type } />
        </div>
      );
    }
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getMessages: (position, radius, page) => {
      dispatch(getMessages(position, radius, page)).then((response) => {
        dispatch(fetchDataSuccess(response.payload.data));
      });
    },
    setGeoPosition: () => {
      dispatch(setGeoPosition());
    },
    getBestMessages: (position, radius) => {
      dispatch(getBestMessages(position, radius)).then((response) => {
        dispatch(fetchBestSuccess(response.payload.data));
      });
    },
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(MessageList);
