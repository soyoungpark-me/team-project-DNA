import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Switch, Route } from 'react-router-dom';
import Loader from 'react-loader-spinner';

import styles from './../styles.css';

import ConversationList from './Conversations/ConversationList';
import DirectMessageList from './DirectMessageList';
import UserList from './../../common/ccu/UserList';
import { timingSafeEqual } from 'crypto';

function mapStateToProps(state) {
  return {
    profile: state.user.profile
  };
}

const Initial = () => (
  <div className="message-list-wrapper">
    empty
  </div>
);

export class DirectComponent extends Component {
  constructor(props){
    super(props);

    this.state = {
      conversationIdx: null
    }
  }

  render() {
    let contents;

    if (this.props.profile !== null) {
      // 필요한 정보가 모두 로드되고 난 후에 렌더링 해줘야 한다.
      contents = (
        <div className='h100'>
          <ConversationList onConversationSelect={
              (selectedConversationIdx) => {
                this.setState({
                  conversationIdx: selectedConversationIdx
                });
              }
            }
            conversationIdx={this.state.conversationIdx} />
          <DirectMessageList conversationIdx={this.state.conversationIdx} />
          <UserList type="direct" />
        </div>
      );
    } else {
      contents = (<Loader type="Oval" color="#8a78b0" height="130" width="130" />);
    }

    return (
      <div className='h100'>
        {contents}
      </div>
    );
  }
}

export default connect(mapStateToProps, null)(DirectComponent);
