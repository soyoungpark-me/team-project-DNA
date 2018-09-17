import React, { Component } from 'react';
import { toast } from 'react-toastify';
import { confirmAlert } from 'react-confirm-alert';
import 'react-confirm-alert/src/react-confirm-alert.css'
import FontAwesome from 'react-fontawesome';
import ReactTooltip from 'react-tooltip';

import { reduxForm, reset, Field } from 'redux-form';
import { connect } from 'react-redux';

import { sendMessage } from './../../../actions/messages/GeoMsgAction';
import { sendMessage as sendDM } from './../../../actions/messages/DirectMsgAction';
import { imageFileUpload } from './../../../actions/helper';

const renderInput = (field) => {
  return (
    <div className="text-input-wrapper">
      <input {...field.input} type="text" id="message-text" autoComplete="off"
        value={field.value}
        onClick={field.onClick} />
    </div>
  )
};

function mapStateToProps(state) {
  return {
    profile: state.user.profile,
    position: state.app.position
  };
}

class MessageForm extends Component{
  constructor(props) {
    super(props);

    this.state = {
      type: "Message",
      file: null,
      value: null
    };

    this.initialImage = this.initialImage.bind(this);
    this.cancelAll = this.cancelAll.bind(this);
    this.setSpeaker = this.setSpeaker.bind(this);
    this.setLocation = this.setLocation.bind(this);
    this.setImage = this.setImage.bind(this);
    this.onChange = this.onChange.bind(this);
  }

  async onSubmit(values) {
    if (this.state.type === "Image") {     
      const formData = new FormData();
      formData.append('image', this.state.file);

      const result = await imageFileUpload(formData, "image");
      if (result.data && result.data.length > 0) {
        values.contents = result.data[0].fileUrl;
      }
    } else if (this.state.type === "Location") {
      values.contents = JSON.stringify(this.props.position);
    }

    if (values.contents !== "") {
      if (this.props.type === "dm") {
        this.props.sendDM(values, this.state.type, this.props.conversationIdx);
      } else { 
        this.props.sendMessage(values, this.state.type);  
      }
    }

    // 전송이 모두 끝난 후엔 초기화!
    values.contents = "";
    this.initialImage();
    this.props.reset('newMessage');
  }

  initialImage() {
    window.$("input:file").val("");
    window.$("#message-text").val("");
    window.$("#message-text").attr("disabled", false);  
    window.$("#message-text").focus();
    window.$(".message-write-fa").css("color", "#bdc6c9");
    this.setState({ type: "Message", file: null });
  }

  setSpeaker() {
    if (this.state.type !== "LoudSpeaker") {
      if (this.state.type !== "Message"){
        toast.error('　확성기 모드로 설정할 수 없습니다!', {
          position: "top-right", autoClose: 2000, pauseOnHover: true,
          hideProgressBar: true, closeOnClick: true, draggable: false
        });
      } else if (this.props.profile.point < 100) {
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
      this.setState({type: "Message"});
      window.$(".message-write-fa").css("color", "#bdc6c9");
    }
  }

  setLocation() {
    this.setState({type: "Location"});
    window.$("#message-text").val("현재 접속 중인 위치를 전송합니다");
  }

  setImage() {   
    window.$("input:file").click();
  }

  onChange(e) {
    this.setState({ 
      file: e.target.files[0],
      type: "Image"
    });

    const fileName = window.$("input:file").val().replace(/^.*[\\\/]/, '');
    const ext = fileName.split(".").pop().toLowerCase();

    if (ext.length > 0) {
      if (!["gif","png","jpg","jpeg"].includes(ext)) {        
        this.initialImage();
        toast.error('git, png, jpg 형식의 파일만 업로드할 수 있습니다.', {
          position: "top-right", autoClose: 2000, pauseOnHover: true,
          hideProgressBar: true, closeOnClick: true, draggable: false
        });
      } else {
        window.$("#message-text").val("이미지를 전송합니다 : " + fileName);
        window.$("#message-text").attr("disabled", true);
      }
    }
  }

  cancelAll() {
    const type = this.state.type;

    confirmAlert({
      title: 'Cancel Image',
      message: `${type === "Image" ? "이미지":"현 위치"} 전송을 취소하시겠습니까?`,
      buttons: [
        {
          label: 'Yes',
          onClick: () => {
            if (type === "Image") {
              this.initialImage();
            } else {
              window.$("#message-text").val("");
              this.setState({type: "Message"});
            }
          }
        },
        {
          label: 'No',
          onClick: () => {}
        }
      ]
    });
  }

  render() {
    const { handleSubmit } = this.props;

    return(
      <form className={`message-write ${this.props.type === "main"?"":"dm-form"}`} 
        encType="multipart/form-data"
        onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        { this.props.type === "main" ?
        <div className="speaker-button-wrapper">
          <button type="button" onClick={this.setSpeaker} data-tip="React-tooltip" className="speaker-button" >
            <FontAwesome className="message-write-fa" name="volume-up" />
          </button>
          <ReactTooltip className='customeTheme' place="top" type="warning" effect="solid">
            <p>확성기로 주변 접속자에게</p>
            <p>푸시 메시지를 보낼 수 있습니다</p>
            <p><strong>100포인트</strong>가 필요합니다</p>
          </ReactTooltip>
        </div> : "" }

        <Field name="contents" component={renderInput} value={this.state.value} />
        
        <button type="button" className={`msg-form-button cancel-button ${this.state.type === "Image" || this.state.type === "Location" ? "active" : ""}`} 
          disabled={this.state.type === "Image" || this.state.type === "Location" ? null : "true" } 
          onClick={this.cancelAll}>
          <span className="ti-close" />
        </button>

        <button type="button" className={`msg-form-button location-button ${this.state.type === "Location" ? "active" : ""}`}
          onClick={this.setLocation} data-tip="" data-for="location">
          <i className={`${this.state.type === "Location" ? "fas" : "far"} fa-compass`}></i>
        </button>
        <ReactTooltip id="location" className='customeTheme' place="top" type="warning" effect="solid">
          <p>현재 위치를 전송합니다</p>
        </ReactTooltip>

        <input type="file" id="file" onChange={this.onChange} />

        <button type="button" className={`msg-form-button image-button ${this.state.type === "Image" ? "active" : ""}`}
          onClick={this.setImage} data-tip="" data-for="image">
          <i className={`${this.state.type === "Image" ? "fas" : "far"} fa-images`}></i>
        </button>
        <ReactTooltip id="image" className='customeTheme' place="top" type="warning" effect="solid">
          <p>이미지를 전송합니다</p>
        </ReactTooltip>

        <div className="message-vl" />

        <button className="msg-form-button msg-submit-button" type="submit">
          <i className="fas fa-location-arrow"></i>
        </button>
      </form>
    )
  }
}
MessageForm = connect(mapStateToProps, { sendMessage, sendDM, reset })(MessageForm);

export default reduxForm({
  form: 'newMessage'
})(MessageForm);
