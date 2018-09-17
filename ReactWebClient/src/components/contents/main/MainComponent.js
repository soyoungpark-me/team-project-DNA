import React, { Component } from 'react';
import { connect } from 'react-redux';
import Loader from 'react-loader-spinner';

import styles from './../styles.css';

import MessageList from './../../common/message/MessageList';
import UserList from './../../common/ccu/UserList';
import MapComponent from './../../common/map/MapComponent';

function mapStateToProps(state) {
  return {
    position: state.app.position,
    profile: state.user.profile,
    socket: state.app.socket
  };
}

export class MainComponent extends Component {
  render() {
    let contents;

    if (this.props.position && this.props.profile && this.props.socket) {
      // 필요한 정보가 모두 로드되고 난 후에 렌더링 해줘야 한다.
      contents = (
        <div className='h100'>
          <MapComponent position={this.props.position} idValue="main-map" />
          <MessageList type="main" />
          <UserList type="main" />
        </div>
      );
    } else {
      contents = (
        <div className='main-with-loader'>      
          <Loader type="Oval" color="#8a78b0" height="130" width="130" />
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

export default connect(mapStateToProps, null)(MainComponent);
