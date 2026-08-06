import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../api/auth';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Role } from '../types/user';

export const RegisterPage: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<Role>('FARMER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await register({ fullName, email, passwordHash: password, role });
      loginUser(res.accessToken, res.user);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '450px', margin: '3rem auto', padding: '0 1rem' }}>
      <Card title="Create Account" subtitle="Join CeyGreen Greenhouse Management">
        {error && <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid var(--danger)', color: 'var(--danger)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.875rem' }}>{error}</div>}
        <form onSubmit={handleSubmit}>
          <Input
            label="Full Name"
            placeholder="John Doe"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
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
            label="Password"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)', fontWeight: 500 }}>
              Account Role
            </label>
            <select
              value={role}
              onChange={(e) => setRole(e.target.value as Role)}
              style={{ width: '100%', padding: '0.75rem 1rem', borderRadius: '10px', background: 'rgba(10, 20, 14, 0.6)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}
            >
              <option value="FARMER">Farmer (Greenhouse & Diagnostics)</option>
              <option value="BUYER">Buyer (Marketplace)</option>
              <option value="ADMIN">Administrator</option>
            </select>
          </div>
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
