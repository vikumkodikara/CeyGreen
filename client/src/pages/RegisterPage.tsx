import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register, login } from '../api/auth';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Role } from '../types/user';

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
      setError('Passwords do not match. Please make sure both fields match.');
      return;
    }

    setLoading(true);

    try {
      // 1. Register user in backend PostgreSQL database
      await register({
        name,
        email,
        password,
        role,
        farmLocation: farmLocation || undefined,
        contactInfo: contactInfo || undefined,
      });

      // 2. Automatically log in to issue OAuth 2.0 JWT access token
      const { token, user } = await login({ email, password });
      loginUser(token.access_token, user);
      navigate('/');
    } catch (err: any) {
      if (err.response?.data) {
        const data = err.response.data;
        if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
          setError(Object.entries(data.fieldErrors).map(([field, msg]) => `${field}: ${msg}`).join('. '));
        } else if (data.message) {
          setError(data.message);
        } else {
          setError('Registration failed. Check password length (min 8 chars) or email uniqueness.');
        }
      } else if (err.request) {
        setError('Cannot connect to user service (Backend server is offline or unreachable).');
      } else {
        setError(err.message || 'Registration failed. Check your input.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <Card title="Create Account" subtitle="Join CeyGreen Greenhouse Management">
        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid var(--danger)', color: 'var(--danger)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}
        <form onSubmit={handleSubmit}>
          <Input
            label="Full Name"
            placeholder="John Doe"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <Input
            label="Email Address"
            type="email"
            placeholder="john@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <Input
            label="Password (min 8 characters)"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
          />
          <div style={{ marginBottom: '1rem' }}>
            <Input
              label="Confirm Password"
              type="password"
              placeholder="••••••••"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              minLength={8}
            />
            {confirmPassword.length > 0 && (
              <div
                style={{
                  fontSize: '0.8rem',
                  marginTop: '-0.5rem',
                  marginBottom: '0.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.3rem',
                  color: password === confirmPassword ? 'var(--success)' : 'var(--danger)',
                  fontWeight: 500,
                }}
              >
                {password === confirmPassword ? (
                  <><span>✓</span> Passwords match</>
                ) : (
                  <><span>✕</span> Passwords do not match</>
                )}
              </div>
            )}
          </div>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)', fontWeight: 500 }}>
              Account Role
            </label>
            <select
              value={role}
              onChange={(e) => setRole(e.target.value as Role)}
              style={{ width: '100%', padding: '0.75rem 1rem', borderRadius: '10px', background: 'rgba(10, 20, 14, 0.75)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
            >
              <option value="FARMER">Farmer (Greenhouse & Diagnostics)</option>
              <option value="BUYER">Buyer (Marketplace)</option>
            </select>
          </div>
          {role === 'FARMER' && (
            <Input
              label="Farm Location (Optional)"
              placeholder="e.g. Nuwara Eliya, Sri Lanka"
              value={farmLocation}
              onChange={(e) => setFarmLocation(e.target.value)}
            />
          )}
          <Input
            label="Contact Info (Optional)"
            placeholder="e.g. +94 77 123 4567"
            value={contactInfo}
            onChange={(e) => setContactInfo(e.target.value)}
          />
          <Button type="submit" isLoading={loading} style={{ width: '100%', marginTop: '1rem' }}>
            Create Account
          </Button>
        </form>
        <p style={{ marginTop: '1.5rem', textAlign: 'center', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--accent-green)' }}>Sign In</Link>
        </p>
      </Card>
    </div>
  );
};
