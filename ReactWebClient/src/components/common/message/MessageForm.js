import React, { Component, PropTypes } from 'react';
import { toast } from 'react-toastify';
import FontAwesome from 'react-fontawesome';
import ReactTooltip from 'react-tooltip';
import { Button } from 'reactstrap';

import { reduxForm, reset, Field } from 'redux-form';
import { connect } from 'react-redux';

import { sendMessage } from './../../../actions/messages/GeoMsgAction';
import { getProfile } from './../../../actions/user/UserAction';

import Message from './Message';

import styles from './styles.css';

const renderInput = (field) => {
  return (
    <div>
      <input {...field.input} type={field.type} className="message-text" autoComplete="off"/>
    </div>
  )
}

function mapStateToProps(state) {
  return {
    profile: state.user.profile
  };
}

class MessageForm extends Component{
  constructor(props) {
    super(props);

    this.state = {
      type: null
    };

    this.setSpeaker = this.setSpeaker.bind(this);
  }

  onSubmit(values) {
    this.props.sendMessage(values, this.state.type);
    if (this.state.type === "LoudSpeaker") {
      this.props.getProfile(this.props.profile.idx);
    }
    this.props.reset('newMessage');
  }

  setSpeaker() {
    if (this.state.type !== "LoudSpeaker") {
      if (this.props.profile.point < 100) {
        toast.error('　포인트가 모자랍니다!', {
          position: "top-right", autoClose: 2000, pauseOnHover: true,
          hideProgressBar: true, closeOnClick: true, draggable: false
        });
      } else {
        toast.success('　확성기 모드로 설정되었습니다!', {
          position: "top-right", autoClose: 2000, pauseOnHover: true,
          hideProgressBar: true, closeOnClick: true, draggable: false
        });
        this.setState({type: "LoudSpeaker"});
        window.$(".message-write-fa").css("color", "#E71D36");
      }
    } else {
      toast.info('　확성기 모드가 해제되었습니다!', {
        position: "top-right", autoClose: 2000, pauseOnHover: true,
        hideProgressBar: true, closeOnClick: true, draggable: false
      });
      this.setState({type: null});
      window.$(".message-write-fa").css("color", "#bdc6c9");
    }

    console.log(this.props.profile);
  }

  render() {
    const { handleSubmit, submitMyForm } = this.props;

    return(
      <form className="message-write" onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        <Field name="contents" component={renderInput} />

        { this.props.type === "main" ?
        <div>
          <button type="button" onClick={this.setSpeaker} data-tip="React-tooltip" className="speaker-button" >
            <FontAwesome className="message-write-fa" name="volume-up" />
          </button>
          <ReactTooltip className='customeTheme' place="top" type="warning" effect="solid">
            <p>확성기로 주변 접속자에게</p>
            <p>푸시 메시지를 보낼 수 있습니다</p>
            <p>(<strong>100포인트</strong>가 필요합니다)</p>
          </ReactTooltip>
        </div> : "" }

        <button className="msg-form-button" type="submit">
          <span className="ti-location-arrow"></span>
        </button>

        <div className="message-vl" />
        <button type="button" className="msg-form-button location-button">
          <span className="ti-location-pin"></span>
        </button>
        <button type="button" className="msg-form-button image-button">
          <span className="ti-image"></span>
        </button>
      </form>
    )
  }
}

function validate(values){
  const errors = {};

  if(!values.contents){
    errors.contents = "Enter a contents";
  }

  return errors;
}

MessageForm = connect(mapStateToProps, { sendMessage, reset })(MessageForm);

export default reduxForm({
  form: 'newMessage'
})(MessageForm);
