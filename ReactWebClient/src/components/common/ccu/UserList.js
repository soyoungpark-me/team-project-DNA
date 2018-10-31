import React, { Component } from 'react';
import Loader from 'react-loader-spinner'
import Dotdotdot from 'react-dotdotdot'
import FontAwesome from 'react-fontawesome';

import { connect } from 'react-redux';

import styles from './styles.css';
import avatar from './../../../../public/images/avatar.png';
import imagePath from './../../../../public/images/empty.png';

function mapStateToProps(state) {
  return {
    users: state.main.users,
    friends: state.direct.users,
    profile: state.user.profile
  };
}

const Profile = (props) => (
  <div className="user-list-my-profile">
    <div className="user-my-profile-top">
      <div className="avatar-wrapper">
        <img className="avatar-image"
          src={props.profile.avatar !== null && props.profile.avatar !== "null" ? 
            props.profile.avatar : avatar} />
      </div>
      <div className="user-my-profile-text">
        <p className="user-my-profile-nickname">{props.profile.nickname}</p>
        <p className="user-my-profile-id">{props.profile.id}</p>
      </div>
    </div>
    <Dotdotdot clamp={2}>
      <p className="user-my-profile-info">{props.profile.description}</p>
    </Dotdotdot>
  </div>
);

class UserList extends Component {
  componentDidUpdate() {
  }

  renderUsers() {
    return this.props.users    
      .map((user) => {
        if(this.props.profile.idx !== parseInt(user.idx)) {
          return (
            <div className="user-list-item" key={user.idx}>
              <div className="avatar-wrapper">
                <img className="avatar-image"
                  src={user.avatar !== null && user.avatar !== "null" ? user.avatar : avatar} />
              </div>
              <p className="user-list-item-nickname">{user.nickname}</p>
              <FontAwesome name="circle" 
                className={`user-list-sign ${(user.inside ? "user-inside" : "user-outside")}`} />
            </div>
          )
        }
      });
  }

  render() {
    let contents;
    if (this.props.users === null) {
      contents = (
        <Loader type="ThreeDots" color="#8a78b0" height="130" width="130" />
      );
    } else if (this.props.users) {
      if (this.props.users.length <= 1) {
        if (this.props.type === "main") {
          contents = (
            <div className="message-list-empty user">
              <span className="ti-face-sad" />
              <p className="user-list-empty">근처에 아직</p>
              <p>접속한 유저가 없습니다</p>
            </div>
          );
        } else {
          contents = (
            <div className="message-list-empty user">
              <span className="ti-face-sad" />
              <p className="user-list-empty">친구 중 아직</p>
              <p>접속한 유저가 없습니다</p>
            </div>
          );
        }
      } else {
        contents = this.renderUsers();
      }
    }

    return (
      <div className="user-list-wrapper">
        <Profile profile={this.props.profile}/>
        <p className="user-list-title"><span className="ti-time"></span>접속 중</p>
        <div className="user-list-contents">
          {contents}
        </div>
      </div>
    );
  }
};

export default connect(mapStateToProps, null)(UserList);
