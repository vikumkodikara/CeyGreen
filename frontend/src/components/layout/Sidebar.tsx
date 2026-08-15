import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';

export const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const { itemCount } = useCart();

  const navItems = [
    { label: 'Dashboard', path: '/', icon: '📊' },
    { label: 'Plant Diagnosis', path: '/diagnosis', icon: '🔬' },
    { label: 'Treatments', path: '/treatments', icon: '💊' },
    { label: 'Greenhouse IoT', path: '/greenhouse', icon: '🌡️' },
    { label: 'Marketplace', path: '/marketplace', icon: '🛒' },
    ...(user?.role === 'BUYER'
      ? [
          { label: `Cart (${itemCount})`, path: '/marketplace/cart', icon: '🧺' },
          { label: 'My Orders', path: '/marketplace/orders', icon: '📦' },
        ]
      : []),
    ...(user?.role === 'FARMER'
      ? [
          { label: 'Farmer Dashboard', path: '/farmer/dashboard', icon: '🌾' },
          { label: 'My Products', path: '/farmer/products', icon: '📋' },
          { label: 'Farmer Orders', path: '/farmer/orders', icon: '🚚' },
        ]
      : []),
    { label: 'Community Forum', path: '/forum', icon: '💬' },
    { label: 'Sales Analytics', path: '/analytics', icon: '📈' },
  ];

  return (
    <aside className="app-sidebar">
      {navItems.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
        >
          <span className="sidebar-icon">{item.icon}</span>
          <span>{item.label}</span>
        </NavLink>
      ))}
    </aside>
  );
};
