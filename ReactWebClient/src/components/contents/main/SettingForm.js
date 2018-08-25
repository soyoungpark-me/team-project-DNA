import React, { Component, PropTypes } from 'react';
import { reduxForm, Field } from 'redux-form';
import { connect } from 'react-redux';
import { Button, Form, FormGroup, Label, Input, FormText } from 'reactstrap';
import FontAwesome from 'react-fontawesome';

import styles from './styles.css';

class SettingForm extends Component{
  constructor(props) {
    super(props);
  }

  onSubmit(props){
    this.props.registerUser(props)
      .then(() => {
        this.setState({ navigate: true })
      });
  }

  render() {
    const { handleSubmit } = this.props;

    return (
      <Form className='setting-wrapper' onSubmit={handleSubmit(this.onSubmit.bind(this))}>
        <p className='setting-title'><FontAwesome name='cog' />Settings</p>
        <div className='setting-item'>
          <span className='setting-name'>익명 설정하기 : </span>
          <Field component="input" className='form-control setting-check' type="checkbox" />{' '}
        </div>
        <div className='setting-item'>
          <span className='setting-name'>반경 변경하기 : </span>
          <Field component="input" className='form-control setting-input' type="number" name="nickname" id="nickname" />
        </div>
        <Button type='submit' className='setting-button'>SAVE</Button>
      </Form>
    );
  };
}

export default reduxForm({
  form: 'setting'
})(SettingForm);
