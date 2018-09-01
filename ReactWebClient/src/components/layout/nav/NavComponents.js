import React, { Component } from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";
import FontAwesome from 'react-fontawesome';
import {
  Button,
  Collapse,
  Navbar,
  NavbarToggler,
  Nav,
  NavItem,
  NavLink,
  Input
} from 'reactstrap';

import styles from './styles.css';
import imagePath from '../../../../public/images/logo.png';

const LoginButton = () => (
  <Button className='nav-button' color="link" href="/login">
    Log In</Button>
);

const SignupButton = () => (
  <Button className='nav-button' color="link" href="/signup">
    Sign Up</Button>
);

export const NavBeforeComponent = () => (
  <Router>
    <div className='nav-bar-wrapper'>
      <Navbar className='nav-bar'>
        <a href='/' className='nav-logo'><img className='nav-logo-img' src={imagePath} /></a>
        <NavLink className='nav-main-item' href="/features"
          style={{left: '75px'}}>
          Features</NavLink>
        <Nav className='nav-items'>
          <Route exact path="/" component={LoginButton} />
          <Route path="/signup" component={LoginButton} />
          <Route path="/login" component={SignupButton} />
          <Route path="/features" component={LoginButton} />
        </Nav>
      </Navbar>
    </div>
  </Router>
);

export class NavAfterComponent extends Component {
  constructor(props) {
    super(props);

    this.toggleNavbar = this.toggleNavbar.bind(this);
    this.state = {
      collapsed: true
    };
  }

  toggleNavbar() {
    this.setState({
      collapsed: !this.state.collapsed
    });
  }
  render() {
    return (
      <div className='nav-bar-wrapper'>
        <Navbar className='nav-bar'>
          <a href='/' className='nav-logo'><img className='nav-logo-img' src={imagePath} /></a>
          <div className='nav-search'>
            <Input type="text" name="search" className="search-input"
                   placeholder="검색어를 입력해주세요." />
            <button><span className="ti-search"></span></button>
          </div>
          <Nav className='nav-items'>
            <NavItem>
              <NavLink href="#"><FontAwesome className='nav-item-fa' name='user-friends' />
              </NavLink>
            </NavItem>
            <NavItem>
              <NavLink href="/dm"><FontAwesome className='nav-item-fa' name='comment' />
              </NavLink>
            </NavItem>
            <NavItem>
              <NavLink href="#"><FontAwesome className='nav-item-fa' name='bell' />
              </NavLink>
            </NavItem>
            <div className="vl" />
            <NavbarToggler style={{ padding: 0, marginLeft: 20 }} onClick={this.toggleNavbar}>
              <NavLink href="#"><FontAwesome className='nav-item-fa' name='bars' />
              </NavLink>
            </NavbarToggler>
          </Nav>

          <Collapse isOpen={!this.state.collapsed} navbar>
            <Nav navbar>
              <NavItem>
                <NavLink href="/profile">프로필 확인</NavLink>
              </NavItem>
              <NavItem>
                <NavLink href="/settings">환경 설정</NavLink>
              </NavItem>
              <div className='hr' />
              <NavItem>
                <NavLink href="/logout">로그아웃</NavLink>
              </NavItem>
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    );
  }
};

// exports NavBeforeComponent;
