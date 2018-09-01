import React, { Component } from 'react';
import { connect } from 'react-redux';

// import markerPng from './../../../../../public/images/marker.png';
import homePng from './../../../../../public/images/home.png';
import styles from './styles.css';

function mapStateToProps(state) {
  return {
    position: state.app.position,
    profile: state.user.profile
  };
}

class MapComponent extends Component {
  constructor(props){
    super(props);

    this.state = {
      initial: true
    };  
  }

  componentDidMount() {
    // 먼저 렌더되어 map을 id로 가진 div가 생성된 후에,
    // componentDidUpdate() 함수가 호출되어 값들을 세팅해줘야 합니다.
    // 때문에 state 값을 바꿔주어 해당 함수를 호출시킵니다.
    
    if (this.state.initial) {
      this.setState({initial: false});
    }
  }

  componentDidUpdate() {
    if (this.props.position !== null) {
      const position = this.props.position;
      const radius = this.props.profile.radius;

      const CURRENT_POSITION = new window.naver.maps.LatLng(position.lat, position.lng);

      var locationBtnHtml = `<a href="" class="btn_mylct"><img class="map-home-button" src="${homePng}"/></a>`;
      var map = new window.naver.maps.Map('map', {
          center: CURRENT_POSITION, //지도의 초기 중심 좌표
          zoom: 9, //지도의 초기 줌 레벨
          minZoom: 1, //지도의 최소 줌 레벨
          zoomControl: true, //줌 컨트롤의 표시 여부
          zoomControlOptions: { //줌 컨트롤의 옵션
              position: window.naver.maps.Position.TOP_RIGHT
          }
      });

      var urlMarker = new window.naver.maps.Marker({
          position: CURRENT_POSITION,
          map: map,
          title: 'urlMarker',
          // icon: markerPng,
          animation: window.naver.maps.Animation.BOUNCE
      });

      var circle = new window.naver.maps.Circle({
          map: map,
          center: CURRENT_POSITION,
          radius,
          fillColor: 'crimson',
          fillOpacity: 0.3,
          strokeColor: 'black',
          strokeOpacity: 0.5
      });

      //customControl 객체를 이용하기
      var customControl = new window.naver.maps.CustomControl(locationBtnHtml, {
          position: window.naver.maps.Position.TOP_LEFT
      });
      var domEventListener = window.naver.maps.Event.addDOMListener(customControl.getElement(), 'click', function() {
          map.setCenter(new window.naver.maps.LatLng(position.lat, position.lng));
      });
      customControl.setMap(map);
    }
  }

  render() {
    return (
      <div id="map" className="geo-chat-map" />
    );
  }
};

export default connect(mapStateToProps, null)(MapComponent);
