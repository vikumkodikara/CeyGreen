import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { useShell } from '../../App';
import { Logo } from './Logo';
import {
  IconHome, IconScan, IconBeaker, IconGauge, IconStore, IconBag,
  IconBox, IconList, IconTruck, IconChat, IconChart,
} from '../icons/Icons';

type Item = { label: string; path: string; icon: React.ReactNode; end?: boolean; badge?: number };

export const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const { itemCount } = useCart();
  const { closeNav } = useShell();
  const isBuyer = user?.role === 'BUYER';
  const isFarmer = user?.role === 'FARMER';

  const grow: Item[] = [
    { label: 'Home', path: '/', icon: <IconHome />, end: true },
    { label: 'Diagnosis', path: '/diagnosis', icon: <IconScan /> },
    { label: 'Treatments', path: '/treatments', icon: <IconBeaker /> },
    { label: 'Greenhouse', path: '/greenhouse', icon: <IconGauge /> },
  ];

  const trade: Item[] = [
    { label: 'Marketplace', path: '/marketplace', icon: <IconStore /> },
    ...(isBuyer
      ? [
          { label: 'Cart', path: '/marketplace/cart', icon: <IconBag />, badge: itemCount },
          { label: 'Orders', path: '/marketplace/orders', icon: <IconBox /> },
        ]
      : []),
    ...(isFarmer
      ? [
          { label: 'Farm sales', path: '/farmer/dashboard', icon: <IconChart /> },
          { label: 'Listings', path: '/farmer/products', icon: <IconList /> },
          { label: 'Fulfillment', path: '/farmer/orders', icon: <IconTruck /> },
        ]
      : []),
  ];

  const more: Item[] = [
    { label: 'Forum', path: '/forum', icon: <IconChat /> },
    { label: 'Analytics', path: '/analytics', icon: <IconChart /> },
  ];

  const render = (items: Item[]) =>
    items.map((item) => (
      <NavLink
        key={item.path}
        to={item.path}
        end={item.end}
        onClick={closeNav}
        className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
      >
        <span className="sidebar-icon">{item.icon}</span>
        <span>{item.label}</span>
        {!!item.badge && <span className="nav-badge">{item.badge}</span>}
      </NavLink>
    ));

  return (
    <aside className="app-sidebar">
      <div className="sidebar-brand">
        <Logo to="/" onLight />
      </div>
      <div className="sidebar-label">Operations</div>
      {render(grow)}
      <div className="sidebar-label">Commerce</div>
      {render(trade)}
      <div className="mode-switch" aria-label="Account mode">
        <span className={isBuyer ? 'on' : ''}>Buyer mode</span>
        <span className={!isBuyer ? 'on' : ''}>Farmer mode</span>
      </div>
      <div className="sidebar-label">Community</div>
      {render(more)}
      <div className="sidebar-foot">© 2026 CeyGreen</div>
    </aside>
  );
};
