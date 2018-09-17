import React, { Component } from 'react';
import { connect } from 'react-redux';
import Loader from 'react-loader-spinner'

import { getMessages, getConversations } from './../../../actions/messages/DirectMsgAction';
import { setGeoPosition } from './../../../actions/AppActions';
import { fetchDataSuccess } from './../../../actions/index';

import Message from './../../common/message/Message';
import MessageForm from './../../common/message/MessageForm';

import styles from './../../common/message/styles.css';

import config from './../../../config';

import imagePath from './../../../../public/images/empty.png';

function mapStateToProps(state) {
  return {
    socket: state.app.socket,
    profile: state.user.profile,
    directs: state.direct.messages,
    avatar: state.direct.avatar
  };
}

class DirectMessageList extends Component {
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
  }

  componentWillMount() {    
    // 1초마다 휠의 위치를 측정합니다.
    const INTERVAL = 1000;
    this.intervalID = setInterval(this.handleInterval, INTERVAL);

    // 5. 서버로부터 새 메시지 이벤트를 받았을 경우에 화면에 새로 렌더링해준다.    
    // 여기선 채팅방 리스트도 갱신해줘야 한다.
    this.props.socket.on('new_dm', (response) => {
      this.props.getConversations(1);
      this.setState({messages: [response.result.dm, ...this.state.messages]});
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
    
    if (nextProps.directs) {
      if (this.page === 1) {
        this.setState({ messages: nextProps.directs });
      } else {
        this.setState({ messages: [...this.state.messages, ...nextProps.directs]});
      }
    }

    if (nextProps.conversationIdx !== this.props.conversationIdx) {
      // 방이 바뀌었을 경우 먼저 초기화해줍니다.
      this.setState({messages: []});
      this.page = 1;
      this.initial = true;
      this.props.getMessages(nextProps.conversationIdx, this.page);
    }
  }

  componentDidUpdate(prevProps, prevState){
    if (this.initial && this.state.messages.length > 0) {
      this.objDiv = document.getElementsByClassName("message-list-chat-wrapper")[0];
      this.scrollToBottom();
      this.initial = false;
      this.beforeHeight = this.objDiv.scrollHeight;
      window.$(".message-list-wrapper > div:first-of-type").hide();
    }

    if (!this.fetching && this.state.position !== null && this.state.position <= 0) {
      if (prevProps.directs && config.PAGINATION_COUNT === prevProps.directs.length) {
          this.beforeHeight = this.objDiv.scrollHeight;
          this.page++;
          this.fetching = true;
          this.props.getMessages(this.props.conversationIdx, this.page);
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
        beforeIdx = message.sender_idx;
        beforeTime = message.created_at.split('T')[0];

        if(currentUser === message.sender_idx) {
          return (
            <Message message={message} key={message._id}
              type={"DM"}
              sender={"me"}
              start={(tempIdx !== beforeIdx) ? true : false }
              dayStart={(tempTime !== beforeTime) ? true : false} />
          )
        } else {
          return (
            <Message message={message} key={message._id}
              type={"DM"}
              sender={"you"}
              avatar={this.props.avatar}
              start={(tempIdx !== beforeIdx) ? true : false }
              dayStart={(tempTime !== beforeTime) ? true : false} />
          )
        }
      });
  }

  render() {
    let contents;

    if (!this.props.conversationIdx) {
      contents = (
        <div className="message-list-empty">
          <p style={{marginTop: "calc(50% + 50px)"}}>내용을 확인할 채팅방을 선택해주세요.</p>
        </div>
      )
    }
    else if (!this.state.messages || this.state.messages === null) {
      window.$(".message-list-wrapper > div:first-of-type").show();
    } else {
      if (this.state.messages.length === 0) {
        contents = (
          <div className="message-list-empty">
            <img src={imagePath} />
            <p>아직 작성된 메시지가 없습니다</p>
          </div>
        );
      } else {
        contents = this.renderMessages();
      }
    }

    return (
      <div className="message-list-wrapper direct-message">
        <Loader type="Oval" color="#8a78b0" height="130" width="130" />
        <div className="message-list-chat-wrapper" style={styles}>
          {contents}
        </div>
        <MessageForm type={"dm"} conversationIdx = {this.props.conversationIdx} />
      </div>
    );  
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
    getConversations: (page) => {
      dispatch(getConversations(page));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(DirectMessageList);
