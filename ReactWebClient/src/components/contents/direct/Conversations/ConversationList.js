import React, { Component } from 'react';
import Loader from 'react-loader-spinner'

import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';

import { getConversations } from './../../../../actions/messages/DirectMsgAction';
import Conversation from './Conversation';

import styles from './styles.css';
import config from './../../../../config';

import imagePath from './../../../../../public/images/empty.png';

function mapStateToProps(state){
  return {
    socket: state.app.socket,
    profile: state.user.profile,
    conversations: state.direct.conversations
  }
}

class ConversationList extends Component {
  constructor(props){
    super(props);

    this.page = 1;
    this.fetchinng = false;

    this.state = {
      position: null,
      conversations: []      
    };

    this.handleInterval = this.handleInterval.bind(this);
    this.handleRequestAnimationFrame = this.handleRequestAnimationFrame.bind(this);
  }

  componentWillMount(){
    this.props.getConversations(this.page);
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
    let newProps;
    if (nextProps.conversations === undefined) {
      newProps = [];
    } else {
      newProps = nextProps.conversations;
    }

    if (this.page === 1) {
      this.setState({ conversations: newProps });
    } else {
      if (nextProps.messages) {
        this.setState({ conversations: [...this.state.conversations, ...newProps]});
      }
    }
  }
  // componentWillUpdate(nextProps){
  //   if(this.props.newMessage !== nextProps.newMessage || nextProps.update){
  //     this.props.getConversations(this.page);
  //   }
  // }
  //
  // componentDidUpdate(prevProps){
  //   if(prevProps.update){
  //     this.props.makeNotUpdate();
  //   }
  // }

  componentDidUpdate() {
    if (this.fetching) {
      window.$(".conversation-list-wrapper > div:first-of-type").show();
    } else {
      window.$(".conversation-list-wrapper > div:first-of-type").hide();
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
    // const item = document.getElementsByClassName("message-list-chat-wrapper");
    // if (item && item.length > 0) {
    //   return item[0].scrollBottom;
    // }
    // return null;
  }

  renderConversations(){
    return this.state.conversations
      // .slice(0, 15 * this.state.page - 1)
      .map((conversation) => {
        return (
          <Conversation          
            clicked={(this.props.conversationIdx === conversation.idx) ? true : false}
            flag={(this.props.conversationIdx === conversation.idx) ? 1 : 0}
            conversation={conversation}
            key={conversation.idx} 
            onConversationClick={this.props.onConversationSelect}/>
        )
    });
  }

  render() {
    if (!this.state.conversations) {
      return (
        <div className='message-list-wrapper'>
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          <div className="message-list-chat-wrapper" />
        </div>
      );
    } else {
      let contents;

      if (this.state.conversations && this.state.conversations.length === 0) {
        contents = (
          <div className="message-list-empty">
            <img src={imagePath} />
            <p>아직 개설된 대화방이 없습니다.</p>
          </div>
        );
      } else {
        contents = this.renderConversations();
      }
      return (
        <div className="conversation-list-wrapper">
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
          {contents}
        </div>
      );
    }
  }
}

export default connect(mapStateToProps, { getConversations })(ConversationList);
