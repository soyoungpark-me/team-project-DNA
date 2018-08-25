import React, { Component } from 'react';
import { connect } from 'react-redux';
import Loader from 'react-loader-spinner'

import { getMessages } from './../../../actions/messages/GeoMsgAction';
import { setGeoPosition } from './../../../actions/AppActions';
import { fetchDataSuccess, fetchDataFailure } from './../../../actions/index';

import Message from './Message';
import MessageForm from './MessageForm';

import styles from './styles.css';
import config from './../../../config';

import imagePath from './../../../../public/images/empty.png';

function mapStateToProps(state) {
  return {
    socket: state.app.socket,
    position: state.app.position,
    profile: state.user.profile,
    messages: state.main.messages,
    directs: state.direct.messages
  };
}

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
      messages: []
    };

    this.handleInterval = this.handleInterval.bind(this);
    this.handleRequestAnimationFrame = this.handleRequestAnimationFrame.bind(this);

    // this.renderMessages = this.renderMessages.bind(this);
    // this.hasToUpdate = this.hasToUpdate.bind(this);
  }

  componentWillMount() {
    if (this.props.type === "main") {
      this.props.getMessages(this.props.position, this.props.profile.radius, this.page);
    } else if (this.props.type === "direct") {
      this.page = -1;
      this.setState({"messages": -1});
    }

    // 1초마다 휠의 위치를 측정합니다.
    const INTERVAL = 1000;
    this.intervalID = setInterval(this.handleInterval, INTERVAL);

    // 5. 서버로부터 새 메시지 이벤트를 받았을 경우에 화면에 새로 렌더링해준다.
    this.props.socket.on('new_msg', (response) => {
      this.setState({messages: [response.result, ...this.state.messages]});
      this.scrollToBottom();
    });
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
    if (this.page === 1) {
      this.setState({ messages: nextProps.messages });
    } else {
      if (nextProps.messages) {
        this.setState({ messages: [...this.state.messages, ...nextProps.messages]});
      }
    }
  }

  componentDidUpdate(prevProps, prevState){
    if (this.initial && this.state.messages !== undefined && this.state.messages !== []) {
      this.objDiv = document.getElementsByClassName("message-list-chat-wrapper")[0];
      this.scrollToBottom();
      this.initial = false;
      this.beforeHeight = this.objDiv.scrollHeight;
      window.$(".message-list-wrapper > div:first-of-type").hide();
    }

    if (!this.fetching && this.state.position !== null && this.state.position <= 0) {
      if (prevProps.messages && config.PAGINATION_COUNT === prevProps.messages.length) {
          this.beforeHeight = this.objDiv.scrollHeight;
          this.page++;

          if (this.props.type === "main") {
            this.props.getMessages(this.props.position, this.props.profile.radius, this.page);
          } else if (this.props.type === "direct"){

          }
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

        if(currentUser === message.user.idx) {
          return (
            <Message message={message} key={message.idx}
              sender={"me"}
              start={(tempIdx !== beforeIdx) ? true : false }
              dayStart={(tempTime !== beforeTime) ? true : false} />
          )
        } else {
          return (
            <Message message={message} key={message.idx}
              sender={"you"}
              start={(tempIdx !== beforeIdx) ? true : false }
              dayStart={(tempTime !== beforeTime) ? true : false} />
          )
        }
      });
  }

  render() {
    console.log(this.props);
    if (!this.state.messages || this.state.messages === null) {
      return (
        <div className='message-list-wrapper'>
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          <div className="message-list-chat-wrapper" />
        </div>
      );
    } else {
      let contents;

      if (this.state.messages.length === 0) {
        contents = (
          <div className="message-list-empty">
            <img src={imagePath} />
            <p>이 근방에서는 아직 작성된 메시지가 없습니다</p>
          </div>
        );
      } else {
        if (this.state.messages === -1){
          contents = "zz";
        } else {
          contents = this.renderMessages();
        }
      }
      return (
        <div className="message-list-wrapper">
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          <div className="message-list-chat-wrapper">
            {contents}
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
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(MessageList);
