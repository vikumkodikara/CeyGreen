import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { login, register } from '../api/auth';
import { useAuth } from '../hooks/useAuth';
import { Input } from '../components/ui/Input';
import { Role } from '../types/user';
import { Logo } from '../components/layout/Logo';
import './AuthPage.css';

export const AuthPage: React.FC = () => {
  const [isRightPanelActive, setIsRightPanelActive] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { loginUser } = useAuth();

  useEffect(() => {
    if (location.pathname === '/register') {
      setIsRightPanelActive(true);
    } else {
      setIsRightPanelActive(false);
    }
  }, [location.pathname]);

  // Login State
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  // Register State
  const [regName, setRegName] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regConfirmPassword, setRegConfirmPassword] = useState('');
  const [regRole, setRegRole] = useState<Role>('FARMER');
  const [regFarmLocation, setRegFarmLocation] = useState('');
  const [regContactInfo, setRegContactInfo] = useState('');
  const [regError, setRegError] = useState('');
  const [regLoading, setRegLoading] = useState(false);

  const enterIotDemo = () => {
    loginUser('iot-demo', {
      id: 'farmer-001',
      email: 'iot-demo@ceygreen.local',
      name: 'IoT Demo Farmer',
      role: 'FARMER',
      farmerId: 'farmer-001',
    });
    navigate('/greenhouse');
  };

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    setLoginLoading(true);
    try {
      if (import.meta.env.VITE_IOT_ONLY === 'true') {
        enterIotDemo();
        return;
      }
      const { token, user } = await login({ email: loginEmail, password: loginPassword });
      loginUser(token.access_token, user);
      navigate('/');
    } catch (err: any) {
      if (err.response?.data?.message) setLoginError(err.response.data.message);
      else if (err.request) setLoginError('Cannot reach the server. Try again shortly.');
      else setLoginError('Sign in failed. Check your email and password.');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setRegError('');
    if (regPassword !== regConfirmPassword) {
      setRegError('Passwords do not match.');
      return;
    }
    setRegLoading(true);
    try {
      await register({
        name: regName,
        email: regEmail,
        password: regPassword,
        role: regRole,
        farmLocation: regFarmLocation || undefined,
        contactInfo: regContactInfo || undefined,
      });
      const { token, user } = await login({ email: regEmail, password: regPassword });
      loginUser(token.access_token, user);
      navigate('/');
    } catch (err: any) {
      if (err.response?.data) {
        const data = err.response.data;
        if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
          setRegError(Object.entries(data.fieldErrors).map(([f, m]) => `${f}: ${m}`).join('. '));
        } else setRegError(data.message || 'Could not create the account.');
      } else if (err.request) setRegError('Cannot reach the server. Try again shortly.');
      else setRegError(err.message || 'Registration failed.');
    } finally {
      setRegLoading(false);
    }
  };

  const switchMode = (mode: 'login' | 'register') => {
    if (mode === 'register') {
      navigate('/register', { replace: true });
    } else {
      navigate('/login', { replace: true });
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-viewport">
        <div className="auth-brand">
          <Logo to="/" />
        </div>

        <div className={`auth-container ${isRightPanelActive ? 'right-panel-active' : ''}`}>

          {/* SIGN UP PANEL */}
          <div className="form-container sign-up-container">
            <form onSubmit={handleRegisterSubmit}>
              <header className="auth-head">
                <h1>Create Account</h1>
                <span className="subtitle">Join CeyGreen to buy and sell farm produce</span>
              </header>

              {regError && (
                <div className="alert alert-error" role="alert">
                  {regError}
                </div>
              )}

              <div className="input-group">
                <Input
                  label="Full name"
                  autoComplete="name"
                  value={regName}
                  onChange={(e) => setRegName(e.target.value)}
                  required
                />
                <Input
                  label="Email"
                  type="email"
                  autoComplete="email"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  required
                />
              </div>

              <div className="field-block">
                <span className="account-type-label" id="account-type-label">
                  Account type
                </span>
                <div className="role-toggle" role="group" aria-labelledby="account-type-label">
                  <button
                    type="button"
                    className={`role-option ${regRole === 'FARMER' ? 'active' : ''}`}
                    aria-pressed={regRole === 'FARMER'}
                    onClick={() => setRegRole('FARMER')}
                  >
                    <strong>Farmer</strong>
                    <small>Diagnose, list, sell</small>
                  </button>
                  <button
                    type="button"
                    className={`role-option ${regRole === 'BUYER' ? 'active' : ''}`}
                    aria-pressed={regRole === 'BUYER'}
                    onClick={() => setRegRole('BUYER')}
                  >
                    <strong>Buyer</strong>
                    <small>Browse and order</small>
                  </button>
                </div>
              </div>

              <div className="input-group">
                <Input
                  label="Password"
                  type="password"
                  autoComplete="new-password"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
                  required
                  minLength={8}
                />
                <Input
                  label="Confirm password"
                  type="password"
                  autoComplete="new-password"
                  value={regConfirmPassword}
                  onChange={(e) => setRegConfirmPassword(e.target.value)}
                  required
                  minLength={8}
                />
              </div>

              {/* Collapses to a single column for buyers, so hiding the farm
                  field cannot leave an empty cell beside the phone input. */}
              <div className={`input-group ${regRole === 'FARMER' ? '' : 'input-group--single'}`}>
                {regRole === 'FARMER' && (
                  <Input
                    label="Farm location (optional)"
                    autoComplete="address-level2"
                    value={regFarmLocation}
                    onChange={(e) => setRegFarmLocation(e.target.value)}
                  />
                )}
                <Input
                  label="Phone (optional)"
                  type="tel"
                  autoComplete="tel"
                  value={regContactInfo}
                  onChange={(e) => setRegContactInfo(e.target.value)}
                />
              </div>

              <div className="auth-actions">
                <button type="submit" className="solid-btn" disabled={regLoading}>
                  {regLoading ? 'Creating account…' : 'Sign Up'}
                </button>
              </div>

              <p className="mobile-toggle">
                Already have an account?{' '}
                <button type="button" onClick={() => switchMode('login')}>
                  Sign in
                </button>
              </p>
            </form>
          </div>

          {/* SIGN IN PANEL */}
          <div className="form-container sign-in-container">
            <form onSubmit={handleLoginSubmit}>
              <header className="auth-head">
                <h1>Welcome Back!</h1>
                <span className="subtitle">Sign in with your email and password</span>
              </header>

              {loginError && (
                <div className="alert alert-error" role="alert">
                  {loginError}
                </div>
              )}

              <div className="input-group input-group--single">
                <Input
                  label="Email"
                  type="email"
                  placeholder="you@farm.lk"
                  autoComplete="email"
                  value={loginEmail}
                  onChange={(e) => setLoginEmail(e.target.value)}
                  required
                />
                <Input
                  label="Password"
                  type="password"
                  placeholder="••••••••"
                  autoComplete="current-password"
                  value={loginPassword}
                  onChange={(e) => setLoginPassword(e.target.value)}
                  required
                />
              </div>

              <button type="button" className="forgot-link">
                Forgot your password?
              </button>

              <div className="auth-actions">
                <button type="submit" className="solid-btn" disabled={loginLoading}>
                  {loginLoading ? 'Signing in…' : 'Sign In'}
                </button>
              </div>

              {import.meta.env.VITE_IOT_ONLY === 'true' && (
                <p className="auth-foot iot-demo">
                  IoT demo is on — Sign in opens the greenhouse without the user service.
                </p>
              )}

              <p className="mobile-toggle">
                New here?{' '}
                <button type="button" onClick={() => switchMode('register')}>
                  Create an account
                </button>
              </p>
            </form>
          </div>

          {/* OVERLAY */}
          <div className="overlay-container">
            <div className="overlay">
              <div className="overlay-panel overlay-left">
                <h1>Already have an account?</h1>
                <p>Sign in to access your dashboard and manage your account.</p>
                <button type="button" className="ghost-btn" onClick={() => switchMode('login')}>
                  Sign In
                </button>
              </div>
              <div className="overlay-panel overlay-right">
                <h1>Join CeyGreen Today!</h1>
                <p>Create an account to start buying and selling farm products.</p>
                <button type="button" className="ghost-btn" onClick={() => switchMode('register')}>
                  Sign Up
                </button>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};
