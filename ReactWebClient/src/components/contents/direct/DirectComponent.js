import React, { Component } from 'react';
import { connect } from 'react-redux';

import styles from './../styles.css';

import ConversationList from './Conversations/ConversationList';
import MessageList from './../../common/message/MessageList';
import UserList from './../../common/ccu/UserList';

function mapStateToProps(state) {
  return {
    profile: state.user.profile
  };
}

export class DirectComponent extends Component {
  render() {
    let contents;

    if (this.props.profile !== null) {
      // 필요한 정보가 모두 로드되고 난 후에 렌더링 해줘야 한다.
      contents = (
        <div className='h100'>
          <ConversationList />
          <MessageList type="direct" />
          <UserList type="direct" />
        </div>
      );
    }

    return (
      <div className='h100'>
        {contents}
      </div>
    );
  }
}

export default connect(mapStateToProps, null)(DirectComponent);
