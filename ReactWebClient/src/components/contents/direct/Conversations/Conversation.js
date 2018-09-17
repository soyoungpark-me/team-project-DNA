import React, { Component } from 'react';
import { connect } from 'react-redux';
import Moment from 'react-moment';
import Dotdotdot from 'react-dotdotdot';

import { fetchOtherProfile } from './../../../../actions/helper';

import avatar from './../../../../../public/images/avatar.png';

function mapStateToProps(state) {
  return {
    profile: state.user.profile
  };
}

class Conversation extends Component {
  constructor(props){
    super(props);

    this.userIdx = this.props.profile.idx;
    this.otherIdx = null;

    let users = this.props.conversation.users;
    this.otherIdx =
      (users[0].idx === this.props.profile.idx)
      ? users[1].idx
      : users[0].idx;

    this.state = {
      otherProfile: null
    }
  }

  async componentWillMount(){
    const otherProfile = await fetchOtherProfile(this.otherIdx);

    this.setState({
      otherProfile: otherProfile.data.result
    });
  }

  render(){
    if (this.state.otherProfile && this.state.otherProfile !== null) {
      let contents = '';
      if (this.props.conversation.last_type === "Image") contents = "[사진]";
      else if (this.props.conversation.last_type === "Location") contents = "[좌표]";
      else contents = this.props.conversation.last_message;

      return (
        <div className={`conversation-list-item ${(this.props.flag) ? 'active' : ''}`}
          onClick={() => this.props.onConversationClick(this.props.conversation.idx)}>
          <div className="conversation-list-left">
            <div className="avatar-wrapper">
              <img className="avatar-image"
                src={this.state.otherProfile.avatar !== null && this.state.otherProfile.avatar !== "null" ?
                  this.state.otherProfile.avatar : avatar }/>
            </div>
          </div>

          <div className="conversation-list-right">
            <p className="conversation-list-nickname">
              {this.state.otherProfile.nickname}
            </p>
            <p className="conversation-list-date">
              <Moment fromNow locale="ko">{this.props.conversation.updated_at}</Moment> &nbsp;
              <Moment format="YYYY/MM/DD">{this.props.conversation.updated_at}</Moment>
            </p>
            <p className="conversation-list-icon"><span className="ion-arrow-right-b"></span></p>
          </div>
          <Dotdotdot clamp={2} className="conversation-list-last-message">
            {contents}
          </Dotdotdot>
        </div>
      )
    } else {
      return (
        <div />
      )
    }
  }
}

export default connect(mapStateToProps, null)(Conversation);
