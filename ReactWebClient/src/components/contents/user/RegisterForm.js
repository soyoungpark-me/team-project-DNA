import React, { Component } from 'react';
import axios from 'axios';
import { Button, Form, FormGroup, Label, FormText } from 'reactstrap';
import { Field, reduxForm } from 'redux-form'
import { BrowserRouter } from 'react-router-dom';

import config from './../../../config';
import styles from './styles.css';

import { imageFileUpload } from './../../../actions/helper';

// validation용 필드
const renderField = ({ input, label, placeholder, type }) => (
  <div>
    <label>{label}</label>
    <div>
      <input {...input} className={`field-${label} form-control`} placeholder={placeholder} type={type} />
    </div>
  </div>
);

class RegisterForm extends Component {
  constructor(props) {
    super(props);
    
    this.state = {
      navigate: false,
      file: null,
      isValid: true
    }

    this.onChange = this.onChange.bind(this);
  }


  async onSubmit(props){
    // 초기화
    this.state.isValid = true;

    ['ID', 'Email', 'Password', 'Confirm_password'].forEach((field) => {
      window.$('.field-'+field).css("border", "");
      window.$('.tag-'+field).hide();

      if (!props[field.toLowerCase()]) {
        window.$('.field-'+field).css("border", "red solid 1px");
        window.$('.tag-'+field).show();

        this.state.isValid = false;
      }
    });

    if (props.email && (!props.email.includes('@') || !props.email.includes('.'))) {
      window.$('.field-Email').css("border", "red solid 1px");
      window.$('.tag-Email').text('이메일의 형식이 잘못되었습니다.');
      window.$('.tag-Email').show();

      this.state.isValid = false;
    }

    if (props.password && props.password.length < 8) {
      window.$('.field-Password').css("border", "red solid 1px");
      window.$('.tag-Password').text('비밀번호는 8자 이상입니다.');
      window.$('.tag-Password').show();

      this.state.isValid = false;

    }

    if (props.password && props.confirm_password && props.password !== props.confirm_password) {
      window.$('.field-Confirm_password').css("border", "red solid 1px");
      window.$('.tag-Confirm_password').text('비밀번호가 서로 일치하지 않습니다.');
      window.$('.tag-Confirm_password').show();

      this.state.isValid = false;
    }

    if (this.state.isValid) {
      const API_URL = `${config.SERVER_HOST}:${config.USER_PORT}/api/users/register`;

      if (this.state.file) {
        const formData = new FormData();
        formData.append("image", this.state.file);

        const result = await imageFileUpload(formData, "profile");
        let avatar = '';
        if (result.data && result.data.length > 0) {
          avatar = result.data[0].fileUrl.replace("dna-edge", "dna-edge-profile");
        }
        props.avatar = avatar;
      }
      console.log(props);

      axios.post(API_URL, props, {})
        .then(response => {
          alert('회원가입이 완료되었습니다!');
        	this.props.history.push('/login');
        })
        .catch(error => {
          if (error.response.data.code === 21400) {
            window.$('.field-ID').css("border", "red solid 1px");
            window.$('.tag-ID').text('이미 존재하는 ID입니다.');
            window.$('.tag-ID').show();
          } else if (error.response.data.code === 22400) {
            window.$('.field-Email').css("border", "red solid 1px");
            window.$('.tag-Email').text('이미 존재하는 이메일입니다.');
            window.$('.tag-Email').show();
          }
        });
    }
  }

  onChange(e) {
    this.setState({ file: e.target.files[0] });

    const fileName = window.$("input:file").val().replace(/^.*[\\\/]/, '');
    window.$(".register-avatar-filename").html(fileName);
  }

  selectFile(e) {
    e.preventDefault();
    window.$("input:file").click();
    const ext = window.$("input:file").val().split(".").pop().toLowerCase();
    if(ext.length > 0){
      if(window.$.inArray(ext, ["gif","png","jpg","jpeg"]) == -1) { 
        alert("gif,png,jpg 파일만 업로드 할수 있습니다.");
        return false;  
      }                  
    }
  }

  render() {
    const { handleSubmit, submitting } = this.props;

    if (this.state.navigate) {
      return (
        <BrowserRouter>
          {this.props.history.push('/login')}
        </BrowserRouter>
      )
    }

    return (
      <Form className='form-wrapper' encType="multipart/form-data"
        onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        <h1 className='form-title'>Welcome to DNA!</h1>
        <hr />
        <div className='form-tab'>
          <p className='form-p form-p-red'>필수 입력 사항</p>
          <FormGroup>
            <Field component={renderField} name="id" type="text"
              label="ID" placeholder="아이디를 입력해주세요." />
            <p className="form-error-tag tag-ID">아이디를 입력해주세요.</p>
          </FormGroup>
          <FormGroup>
            <Field component={renderField} name="email" type="text"
              label="Email" placeholder="이메일은 입력해주세요." />
            <p className="form-error-tag tag-Email">이메일를 입력해주세요.</p>
          </FormGroup>
          <FormGroup>
            <Field component={renderField} name="password" type="password"
              label="Password" placeholder="비밀번호를 입력해주세요." />
            <p className="form-error-tag tag-Password">비밀번호를 입력해주세요.</p>
          </FormGroup>
          <FormGroup>
            <Field component={renderField} name="confirm_password" type="password"
              label="Confirm_password" placeholder="비밀번호를 확인해주세요." />
            <p className="form-error-tag tag-Confirm_password">비밀번호 확인를 입력해주세요.</p>
          </FormGroup>
        </div>

        <div className='form-tab'>
          <p className='form-p'>(선택 입력 사항)</p>
          <FormGroup>
            <Label for="exampleEmail">Nickname</Label>
            <Field component="input" className='form-control' type="text" name="nickname" id="nickname" placeholder="별명을 알려주세요." />
          </FormGroup>
          <FormGroup>
            <Label for="exampleText">Introduce</Label>
            <Field component="textarea" className='form-control' type="textarea" name="text" id="description" placeholder="자신을 간단하게 소개해주세요." />
          </FormGroup>
          <FormGroup>
            <Label for="exampleFile">Avatar</Label>
            <input type="file" id="file" onChange={this.onChange} />
            <div className="register-avatar-wrapper">
              <button className="register-avatar-button" onClick={this.selectFile}>파일 선택</button>
              <span className="register-avatar-filename">파일을 선택해주세요.</span>              
            </div>
            <FormText color="muted">
              프로필 사진을 선택해주세요.
            </FormText>
          </FormGroup>
        </div>
        <Button type='submit' disabled={submitting} className='form-button'>SIGN UP</Button>
      </Form>
    );
  };
};

export default reduxForm({
  form: 'register'
})(RegisterForm);
