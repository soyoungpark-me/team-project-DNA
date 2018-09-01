import React, { Component } from 'react';
import Moment from 'react-moment';
import FontAwesome from 'react-fontawesome';
import 'moment/locale/ko';


import { connect } from 'react-redux';
import { applyLike } from './../../../actions/messages/GeoMsgAction';

import deco from './../../../../public/images/deco.png';
import megaphone from './../../../../public/images/megaphone.png';

const DayStart = (props) => (
  <div className="start-date-wrapper">
    <hr/>
    <Moment locale="ko" format="YYYY년 MM월 DD일" className="message-start-date">
        {props.date}
    </Moment>
  </div>
);

const CreatedAt = (props) => (
  <Moment locale="ko" format="A hh:mm" className="message-created-at">
      {props.date}
  </Moment>
);

class Message extends Component {
  constructor() {
    super();

    this.onThumbClick = this.onThumbClick.bind(this);
  }
  onThumbClick() {
    this.props.applyLike(this.props.message.idx);
  }

  render() {
    let avatarPath;

    if (this.props.type === "DM") {
      avatarPath = this.props.avatar;
    } else {
      avatarPath = this.props.message.user.avatar;
    }

    return (
      <div className={`bubble-wrapper wrapper-${this.props.sender}`}>    
        {(this.props.dayStart) ? <DayStart date={this.props.message.created_at}/> : ''}
        
        {(this.props.sender === 'me') ? 
        <div className="bubble-side-wrapper">
          {(this.props.type === "DM") ? "" : 
            <div onClick={this.onThumbClick}
              className={`message-thumb-up ${(this.props.message.likes.includes(this.props.idx)) ? "i-liked-it" : ""}`}>
              <FontAwesome className="message-thumb-up-fa" name="thumbs-up" />
              <span className="message-thumb-up-count">{this.props.message.like_count}</span>
            </div>
          }
          <CreatedAt date={this.props.message.created_at} />
        </div> : ''}    
        {(this.props.start && this.props.sender === 'you')
          ?
          <div className="bubble-profile-wrapper">
            <div className="avatar-wrapper">
              <img className="avatar-image"
                src={(avatarPath) !== null ?
                  avatarPath :
                  "/../public/img/avatar.png"}/>
            </div>
            <p className='bubble-title-name'>
              {(this.props.type === "DM") ? "" : this.props.message.user.nickname}
            </p>
          </div>
          : ''}
        <div className={`bubble bubble-${(this.props.message.type === "LoudSpeaker")
          ? "speaker" : ""} bubble-${this.props.sender } start-${this.props.start}`}>        
            <span className="bubble-triangle"/>
            <div className="bubble-contents">
              {(this.props.message.type === "Image") ? 
                <img className="bubble-image" src={this.props.message.contents} /> : this.props.message.contents}
            </div>
            {(this.props.message.type === "LoudSpeaker")
              ? (<div><img src={megaphone} /><img src={deco}/></div>) : ""}
        </div>
        {(this.props.sender === 'you') ? 
        <div className="bubble-side-wrapper">
          {(this.props.type === "DM") ? "" : 
            <div onClick={this.onThumbClick}
            className={`message-thumb-up ${(this.props.message.likes.includes(this.props.idx)) ? "i-liked-it" : ""}`}>
              <FontAwesome className="message-thumb-up-fa" name="thumbs-up" />
              <span className="message-thumb-up-count">{this.props.message.like_count}</span>
            </div>
          }
          <CreatedAt date={this.props.message.created_at} />
        </div> : ''}    
      </div>
    );
  }
};

export default connect(null, { applyLike })(Message);
