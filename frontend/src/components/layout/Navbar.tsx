import React, { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useShell } from '../../App';
import { IconBell, IconLogout, IconMenu, IconSearch } from '../icons/Icons';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logoutUser } = useAuth();
  const { toggleNav } = useShell();
  const navigate = useNavigate();
  const [notesOpen, setNotesOpen] = useState(false);
  const searchRef = useRef<HTMLInputElement>(null);
  const initial = (user?.fullName || user?.name || 'U').trim().charAt(0).toUpperCase();
  const roleLabel = user?.role === 'BUYER' ? 'Buyer' : user?.role === 'ADMIN' ? 'Admin' : 'Farmer';

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        searchRef.current?.focus();
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

  return (
    <header className="app-navbar">
      <div className="nav-left">
        {isAuthenticated && (
          <button type="button" className="nav-icon-btn" aria-label="Toggle menu" onClick={toggleNav}>
            <IconMenu />
          </button>
        )}
      </div>
      {isAuthenticated && (
        <label className="nav-search">
          <IconSearch />
          <input ref={searchRef} type="search" placeholder="Search something..." />
          <kbd>⌘ K</kbd>
        </label>
      )}
      <div className="nav-actions">
        {isAuthenticated ? (
          <>
            <div className="nav-bell-wrap">
              <button type="button" className="nav-icon-btn" aria-label="Alerts" onClick={() => setNotesOpen((v) => !v)}>
                <IconBell />
                <span className="nav-count">3</span>
              </button>
              {notesOpen && (
                <div className="nav-dropdown">
                  <p><strong>Humidity high</strong> in ZONE1 — vent the house.</p>
                  <p><strong>New order</strong> waiting on fulfillment.</p>
                  <p><strong>Forum reply</strong> on the blight thread.</p>
                  <Link to="/greenhouse" onClick={() => setNotesOpen(false)}>Open greenhouse</Link>
                </div>
              )}
            </div>
            <div className="user-chip">
              <div className="user-avatar">{initial}</div>
              <div className="user-meta">
                <strong>{user?.fullName || user?.name}</strong>
                <span>{roleLabel}</span>
              </div>
            </div>
            <button type="button" className="nav-text-btn" onClick={() => { logoutUser(); navigate('/login'); }}>
              <IconLogout /> Sign out
            </button>
          </>
        ) : (
          <>
            <Link to="/login"><button type="button" className="nav-text-btn">Sign in</button></Link>
            <Link to="/register"><button type="button" className="nav-text-btn">Join</button></Link>
          </>
        )}
      </div>
    </header>
  );
};
