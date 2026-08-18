import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/auth';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Logo } from '../components/layout/Logo';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (import.meta.env.VITE_IOT_ONLY === 'true') {
        enterIotDemo();
        return;
      }
      const { token, user } = await login({ email, password });
      loginUser(token.access_token, user);
      navigate('/');
    } catch (err: any) {
      if (err.response?.data?.message) setError(err.response.data.message);
      else if (err.request) setError('Cannot reach the server. Try again shortly.');
      else setError('Sign in failed. Check your email and password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div style={{ width: '100%', maxWidth: 420 }}>
        <div style={{ marginBottom: '1.25rem', display: 'flex', justifyContent: 'center' }}>
          <Logo to="/" />
        </div>
        <div className="auth-card">
          <h2>Sign in</h2>
          <p className="lead">White card on the green house. Enter your account.</p>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <Input label="Email" type="email" placeholder="you@farm.lk" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <Input label="Password" type="password" placeholder="••••••••" value={password} onChange={(e) => setPassword(e.target.value)} required />
            <Button type="submit" isLoading={loading} style={{ width: '100%', marginTop: '0.35rem' }}>
              Enter
            </Button>
          </form>
          {import.meta.env.VITE_IOT_ONLY === 'true' && (
            <p className="auth-foot">
              IoT demo is on — Enter opens the greenhouse without the user service.
            </p>
          )}
          <p className="auth-foot">
            New here? <Link to="/register">Create an account</Link>
          </p>
        </div>
      </div>
    </div>
  );
};
