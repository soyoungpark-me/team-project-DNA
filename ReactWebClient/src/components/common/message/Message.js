import React, { Component } from 'react';
import Moment from 'react-moment';
import 'moment/locale/ko';

import styles from './styles.css';
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

const Message = (props) => (
  <div className={`bubble-wrapper wrapper-${props.sender}`}>
  {(props.dayStart) ? <DayStart date={props.message.created_at}/> : ''}
  {(props.sender === 'me') ? <CreatedAt date={props.message.created_at} /> : ''}
  {(props.start && props.sender === 'you')
    ?
    <div className="bubble-profile-wrapper">
      <div className="avatar-wrapper">
        <img className="avatar-image"
          src={(props.message.user.avatar) !== null ?
            props.message.user.avatar :
            "/../public/img/avatar.png"}/>
      </div>
      <p className='bubble-title-name'>
        {props.message.user.nickname}
      </p>
    </div>
    : ''}
  <div className={`bubble bubble-${(props.message.type === "LoudSpeaker")
    ? "speaker" : ""} bubble-${props.sender } start-${props.start}`}>
      <span className="bubble-triangle"/>
      {props.message.contents}
      {(props.message.type === "LoudSpeaker")
        ? (<div><img src={megaphone} /><img src={deco}/></div>) : ""}
  </div>
  {(props.sender === 'you') ? <CreatedAt date={props.message.created_at} /> : ''}
  </div>
);

export default Message;
