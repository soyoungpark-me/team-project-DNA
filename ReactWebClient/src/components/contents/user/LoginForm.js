import React, { Component } from 'react';
import { connect } from 'react-redux';

import axios from 'axios';
import { Button, Form, FormGroup } from 'reactstrap';
import { Field, reduxForm } from 'redux-form'
import history from './../../../history';

import { getProfile, setUserIndex } from './../../../actions/user/UserAction';

import config from './../../../config';
import styles from './styles.css';

// validation용 필드
const renderField = ({ input, label, placeholder, type }) => (
  <div>
    <label>{label}</label>
    <div>
      <input {...input} className={`field-${label} form-control`} placeholder={placeholder} type={type} />
    </div>
  </div>
);

class LoginForm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      navigate: false,
      isValid: true
    }

    this.submitted = false;
  }  
  
  onSubmit(props){
    // 초기화
    this.setState({isValid: true}); 

    ['ID', 'Password'].forEach((field) => {
      window.$('.field-'+field).css("border", "");
      window.$('.tag-'+field).hide();

      if (!props[field.toLowerCase()]) {
        window.$('.field-'+field).css("border", "red solid 1px");
        window.$('.tag-'+field).show();

        this.setState({isValid: false});
      }
    });

    if (props.password && props.password.length < 8) {
      window.$('.field-Password').css("border", "red solid 1px");
      window.$('.tag-Password').text('비밀번호는 8자 이상입니다.');
      window.$('.tag-Password').show();

      this.setState({isValid: false});
    }

    if (this.state.isValid) {
      // if (this.submitted) {
      //   alert("요청이 전송되었습니다. 잠시만 기다려주세요!");
      // } else {
        this.submitted = true;
        
        const API_URL = `${config.SERVER_HOST}:${config.USER_PORT}/api/users/login`;
        axios.post(API_URL, props, {mode: "no-cors"})
        .then(async (response) => {
            const result = response.data.result;
            let token = result.token;
            token.idx = result.profile.idx;
            sessionStorage.setItem("token", JSON.stringify(token));

            // 다음으로 프로필을 저장한다.
            await this.props.getProfile(result.profile.idx);
            await this.props.setUserIndex(result.profile.idx);

            // 그리고 메인으로 이동한다.
            history.push('/main');
            window.location.reload();
          })
        .catch(error => {
            console.dir(error);
            if (error.response) {
              if (error.response.data.code === 23400) {
                window.$('.field-ID').css("border", "red solid 1px");
                window.$('.tag-ID').text('존재하지 않는 ID입니다.');
                window.$('.tag-ID').show();
              } else if (error.response.data.code === 24400) {
                window.$('.field-Password').css("border", "red solid 1px");
                window.$('.tag-Password').text('비밀번호가 일치하지 않습니다.');
                window.$('.tag-Password').show();
              }
            }
        });
      // }
    }
  }

  componentWillMount() {
    if (sessionStorage.getItem("token")) {
      history.push('/main');
    }
  }

  render() {
    const { handleSubmit, submitting } = this.props;

    return (
      <Form className='form-wrapper' onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        <h1 className='form-title'>Welcome to DNA!</h1>
        <hr />
        <div className='login-form-tab'>
          <FormGroup>
            <Field component={renderField} name="id" type="text"
              label="ID" placeholder="아이디를 입력해주세요." />
            <p className="form-error-tag tag-ID">아이디를 입력해주세요.</p>
          </FormGroup>
          <FormGroup>
            <Field component={renderField} name="password" type="password"
              label="Password" placeholder="비밀번호를 입력해주세요." />
            <p className="form-error-tag tag-Password">비밀번호를 입력해주세요.</p>
          </FormGroup>
          <Button type='submit' disabled={submitting} className='form-button'>LOG IN</Button>
        </div>
      </Form>
    );
  };
};

LoginForm = connect(null, { getProfile, setUserIndex })(LoginForm);

export default reduxForm({
  form: 'login'
})(LoginForm);
