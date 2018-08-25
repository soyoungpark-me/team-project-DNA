import React from "react";
import { BrowserRouter, Route } from 'react-router-dom';

/* import Components */
import { NavBeforeComponent } from './nav/NavComponents';
import LoginForm from './../contents/user/LoginForm';
import RegisterForm from './../contents/user/RegisterForm';

const BeforeLoginLayout = () => (
  <div className="h100">
    <NavBeforeComponent />
    <BrowserRouter>
      <div className="h100calc contents-wrapper">
          <Route exact path="/signup" component={RegisterForm} />
          <Route exact path="/login" component={LoginForm} />
          <Route exact path="/" component={LoginForm} />
      </div>
    </BrowserRouter>
  </div>
);

export default BeforeLoginLayout;
