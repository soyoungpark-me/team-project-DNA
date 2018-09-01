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
    <div>
      <input {...field.input} type="text" className="message-text" autoComplete="off"
       onClick={field.onClick} />
    </div>
  )
};

function mapStateToProps(state) {
  return {
    profile: state.user.profile
  };
}

class MessageForm extends Component{
  constructor(props) {
    super(props);

    this.state = {
      type: null,
      file: null
    };

    this.initialImage = this.initialImage.bind(this);
    this.setSpeaker = this.setSpeaker.bind(this);
    this.selectFile = this.selectFile.bind(this);
    this.onChange = this.onChange.bind(this);
    this.cancelUpload = this.cancelUpload.bind(this);
  }

  async onSubmit(values) {
    if (this.state.type === "Image") {     
      const formData = new FormData();
      formData.append('image', this.state.file);

      const result = await imageFileUpload(formData);
      values.contents = result.data;
    }

    if (this.props.type === "dm") {
      this.props.sendDM(values, this.state.type, this.props.conversationIdx);
    } else { 
      this.props.sendMessage(values, this.state.type);  
    }

    // 전송이 모두 끝난 후엔 초기화!
    this.initialImage();
    this.props.reset('newMessage');
  }

  initialImage() {
    window.$("input:file").val("");
    window.$(".message-text").val("");
    window.$(".message-text").attr("readonly", false);  
    window.$(".message-write-fa").css("color", "#bdc6c9");
    this.setState({ type: "", file: null });
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
  }

  onChange(e) {
    this.setState({ 
      file: e.target.files[0],
      type: "Image"
    });

    const fileName = window.$("input:file").val().replace(/^.*[\\\/]/, '');
    window.$("input:text").val("이미지를 전송합니다 : " + fileName);
    window.$(".message-text").attr("readonly", true);
  }

  selectFile() {   
    if (this.state.type !== "Image") { // 이미지가 이미 업로드된 상태가 아니라면
      window.$("input:file").click();
      const ext = window.$("input:file").val().split(".").pop().toLowerCase();
      if(ext.length > 0){
        if(window.$.inArray(ext, ["gif","png","jpg","jpeg"]) == -1) { 
          alert("gif,png,jpg 파일만 업로드 할수 있습니다.");
          return false;  
        }                  
      }
    }
  }

  cancelUpload() {
    if (this.state.type === "Image") { // 이미지가 이미 업로드된 상태라면
      confirmAlert({
        title: 'Cancel Image',
        message: "이미지 전송을 취소하시겠습니까?",
        buttons: [
          {
            label: 'Yes',
            onClick: () => {
              this.initialImage();
            }
          },
          {
            label: 'No',
            onClick: () => {
              const fileName = window.$("input:file").val().replace(/^.*[\\\/]/, '');
              window.$("input:text").val("이미지를 전송합니다 : " + fileName);
            }
          }
        ]
      });
    }
  }

  render() {
    const { handleSubmit } = this.props;

    return(
      <form className="message-write" encType="multipart/form-data"
        onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        <Field name="contents" component={renderInput} onClick={this.cancelUpload} />

        { this.props.type === "main" ?
        <div>
          <button type="button" onClick={this.setSpeaker} data-tip="React-tooltip" className="speaker-button" >
            <FontAwesome className="message-write-fa" name="volume-up" />
          </button>
          <ReactTooltip className='customeTheme' place="top" type="warning" effect="solid">
            <p>확성기로 주변 접속자에게</p>
            <p>푸시 메시지를 보낼 수 있습니다</p>
            <p><strong>100포인트</strong>가 필요합니다</p>
          </ReactTooltip>
        </div> : "" }

        <button className="msg-form-button" type="submit">
          <span className="ti-location-arrow"></span>
        </button>

        <div className="message-vl" />
        <button type="button" className="msg-form-button location-button">
          <span className="ti-location-pin"></span>
        </button>
        <input type="file" id="file" onChange={this.onChange} />
        <Field name="image" component={renderInput} onClick={this.cancelUpload} />
        <button type="button" className="msg-form-button image-button" onClick={this.selectFile}>
          <span className="ti-image"></span>
        </button>
      </form>
    )
  }
}
MessageForm = connect(mapStateToProps, { sendMessage, sendDM, reset })(MessageForm);

export default reduxForm({
  form: 'newMessage'
})(MessageForm);
