import React from 'react';
import { NavLink } from 'react-router-dom';

export const Sidebar: React.FC = () => {
  const navItems = [
    { label: 'Dashboard', path: '/', icon: '📊' },
    { label: 'Plant Diagnosis', path: '/diagnosis', icon: '🔬' },
    { label: 'Treatments', path: '/treatments', icon: '💊' },
    { label: 'Greenhouse IoT', path: '/greenhouse', icon: '🌡️' },
    { label: 'Marketplace', path: '/marketplace', icon: '🛒' },
    { label: 'Community Forum', path: '/forum', icon: '💬' },
    { label: 'Sales Analytics', path: '/analytics', icon: '📈' },
  ];

  return (
    <aside
      style={{
        width: '240px',
        padding: '1.5rem 1rem',
        borderRight: '1px solid var(--border-color)',
        minHeight: 'calc(100vh - 70px)',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
      }}
    >
      {navItems.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          style={({ isActive }) => ({
            display: 'flex',
            alignItems: 'center',
            gap: '0.75rem',
            padding: '0.75rem 1rem',
            borderRadius: '10px',
            textDecoration: 'none',
            color: isActive ? '#051d0d' : 'var(--text-muted)',
            background: isActive
              ? 'linear-gradient(135deg, var(--accent-green) 0%, var(--accent-emerald) 100%)'
              : 'transparent',
            fontWeight: isActive ? 600 : 400,
            transition: 'all 0.2s ease',
          })}
        >
          <span style={{ fontSize: '1.2rem' }}>{item.icon}</span>
          <span>{item.label}</span>
        </NavLink>
      ))}
    </aside>
  );
};
