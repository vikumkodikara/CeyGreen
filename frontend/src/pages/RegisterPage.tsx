import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register, login } from '../api/auth';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Role } from '../types/user';
import { Logo } from '../components/layout/Logo';

export const RegisterPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState<Role>('FARMER');
  const [farmLocation, setFarmLocation] = useState('');
  const [contactInfo, setContactInfo] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    setLoading(true);
    try {
      await register({
        name, email, password, role,
        farmLocation: farmLocation || undefined,
        contactInfo: contactInfo || undefined,
      });
      const { token, user } = await login({ email, password });
      loginUser(token.access_token, user);
      navigate('/');
    } catch (err: any) {
      if (err.response?.data) {
        const data = err.response.data;
        if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
          setError(Object.entries(data.fieldErrors).map(([f, m]) => `${f}: ${m}`).join('. '));
        } else setError(data.message || 'Could not create the account.');
      } else if (err.request) setError('Cannot reach the server. Try again shortly.');
      else setError(err.message || 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
        <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'center' }}>
          <Logo to="/" />
        </div>
        <div className="auth-card" style={{ maxWidth: 420, margin: '0 auto' }}>
        <h2>Create account</h2>
        <p className="lead">Farmer or buyer — pick a side and go.</p>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <Input label="Full name" value={name} onChange={(e) => setName(e.target.value)} required />
          <Input label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          <p style={{ fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.4rem', color: 'var(--ink-2)' }}>Account type</p>
          <div className="role-toggle">
            <button type="button" className={`role-option ${role === 'FARMER' ? 'active' : ''}`} onClick={() => setRole('FARMER')}>
              <strong>Farmer</strong>
              <small>Diagnose, list, sell</small>
            </button>
            <button type="button" className={`role-option ${role === 'BUYER' ? 'active' : ''}`} onClick={() => setRole('BUYER')}>
              <strong>Buyer</strong>
              <small>Browse and order</small>
            </button>
          </div>
          <Input label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={8} />
          <Input label="Confirm password" type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required minLength={8} />
          {role === 'FARMER' && (
            <Input label="Farm location (optional)" value={farmLocation} onChange={(e) => setFarmLocation(e.target.value)} />
          )}
          <Input label="Phone (optional)" value={contactInfo} onChange={(e) => setContactInfo(e.target.value)} />
          <Button type="submit" isLoading={loading} style={{ width: '100%', marginTop: '0.2rem' }}>
            Create account
          </Button>
        </form>
        <p className="auth-foot">
          Already registered? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
};
