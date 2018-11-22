import React, { Component } from 'react';
import Moment from 'react-moment';
import FontAwesome from 'react-fontawesome';
import 'moment/locale/ko';

import { connect } from 'react-redux';
import { applyLike } from './../../../actions/messages/GeoMsgAction';

import MapComponent from './../map/MapComponent';
import deco from './../../../../public/images/deco.png';
import megaphone from './../../../../public/images/megaphone.png';
import avatar from './../../../../public/images/avatar.png';
import config from './../../../config';

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
    this.searchAddressToCoordinate = this.searchAddressToCoordinate.bind(this);

    this.state = {
      address: ''
    }
  }

  onThumbClick() {
    this.props.applyLike(this.props.message.idx);
  }
  
  componentDidMount() {
    if (this.props.message.type === "Location") {
      this.searchAddressToCoordinate();     
    }
  }

  searchAddressToCoordinate() {    
    const position = JSON.parse(this.props.message.contents);      
    const CURRENT_POSITION = new window.naver.maps.LatLng(position.lat, position.lng);

    window.naver.maps.Service.reverseGeocode({location: CURRENT_POSITION}, function(status, response) {
      if (status !== window.naver.maps.Service.Status.OK) {
        console.log ('검색 결과가 없거나 기타 네트워크 에러');
      } else {
        const address = response.result.items[0].address;
        window.$(".message-address-contents").html(address);
      }
    });
  }    

  render() {
    let avatarPath, nickname;
    const user = this.props.message.user;

    if (this.props.type === "DM") {
      avatarPath = this.props.avatar;
      nickname = "";
    } else {
      avatarPath = user.avatar;
      if (user.anonymity === 1) {
        nickname = config.ADJECTIVE[user.idx & 100] + " " + config.ANIMAL[user.idx % 100];
      } else {
        nickname = user.nickname;
      }
    }

    return (
      <div className={`bubble-wrapper wrapper-${this.props.sender} ${this.props.message.type}`}>    
        {this.props.dayStart ? <DayStart date={this.props.message.created_at}/> : ''}
        
        {this.props.sender === 'me' ? 
        <div className="bubble-side-wrapper">
          {this.props.type === "DM" ? "" : 
            <div onClick={this.onThumbClick}
              className={`message-thumb-up ${(this.props.message.likes.includes(this.props.idx)) ? "i-liked-it" : ""}`}>
              <FontAwesome className="message-thumb-up-fa" name="star" />
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
                src={this.props.message.user.anonymity === 0 && avatarPath !== null && avatarPath !== "null" 
                ? avatarPath : avatar} />
            </div>
            <p className='bubble-title-name'>
              {this.props.type === "DM" ? "" : nickname}
            </p>
          </div>
          : ''}
        <div className={`bubble bubble-${(this.props.message.type === "LoudSpeaker")
          ? "speaker" : ""} bubble-${this.props.sender } start-${this.props.start}`}>        
            <span className="bubble-triangle"/>
            <div className="bubble-contents">
              {this.props.message.type === "Message" || this.props.message.type === "LoudSpeaker" ?
                this.props.message.contents : ""}
              {this.props.message.type === "Share" ? "[DNA] 모바일에서만 볼 수 있는 메시지 타입입니다" : ""}
              {this.props.message.type === "Image" ? 
                <img className="bubble-image" src={this.props.message.contents} /> : ""}
              {this.props.message.type === "Location" ? 
                <div>
                  <MapComponent position={JSON.parse(this.props.message.contents)} classValue="message-map"
                    idValue={`message-${this.props.message.idx}`} />
                  <p className="message-address">
                    <span className="ti-location-pin"/>
                    <strong>위치 : </strong>
                    <span className="message-address-contents"/></p>
                </div> : "" }
            </div>
            {this.props.message.type === "LoudSpeaker"
              ? (<div><img src={megaphone} /><img src={deco}/></div>) : ""}
        </div>
        {this.props.sender === 'you' ? 
        <div className="bubble-side-wrapper">
          {this.props.type === "DM" ? "" : 
            <div onClick={this.onThumbClick}
            className={`message-thumb-up ${(this.props.message.likes.includes(this.props.idx)) ? "i-liked-it" : ""}`}>
              <FontAwesome className="message-thumb-up-fa" name="star" />
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
