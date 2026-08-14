import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Button } from '../ui/Button';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logoutUser } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logoutUser();
    navigate('/login');
  };

  return (
    <nav
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0.75rem 1.25rem',
        borderBottom: '1px solid var(--border-color)',
        background: 'rgba(11, 19, 14, 0.85)',
        backdropFilter: 'blur(10px)',
        position: 'sticky',
        top: 0,
        zIndex: 50,
      }}
    >
      <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', textDecoration: 'none' }}>
        <div
          style={{
            width: '38px',
            height: '38px',
            borderRadius: '10px',
            background: 'linear-gradient(135deg, var(--accent-green), var(--accent-teal))',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#051d0d',
            fontWeight: 700,
            fontSize: '1.2rem',
          }}
        >
          🌱
        </div>
        <span style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--text-main)' }}>
          Cey<span style={{ color: 'var(--accent-green)' }}>Green</span>
        </span>
      </Link>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        {isAuthenticated ? (
          <>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              👤 {user?.fullName} ({user?.role})
            </span>
            <Button variant="secondary" size="sm" onClick={handleLogout}>
              Logout
            </Button>
          </>
        ) : (
          <>
            <Link to="/login">
              <Button variant="ghost" size="sm">Login</Button>
            </Link>
            <Link to="/register">
              <Button variant="primary" size="sm">Register</Button>
            </Link>
          </>
        )}
      </div>
    </nav>
  );
};
