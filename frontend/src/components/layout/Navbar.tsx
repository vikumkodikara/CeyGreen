import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useShell } from '../../App';
import { IconBell, IconLogout, IconMenu, IconSearch } from '../icons/Icons';
import { getNotificationHistory, NotificationItem } from '../../api/notifications';

/* ─── Helpers ────────────────────────────────────────────────────── */
const TOPIC_ICON: Record<string, string> = {
  'order-events':      '🛒',
  'greenhouse-alerts': '🌡️',
  'diagnosis-events':  '🔬',
  'treatment-events':  '💊',
  'stock-events':      '📦',
  'forum-events':      '💬',
};

function topicIcon(topicOrType?: string): string {
  if (!topicOrType) return '🔔';
  const key = topicOrType.toLowerCase();
  if (key.includes('order') || key.includes('cart')) return '🛒';
  if (key.includes('greenhouse') || key.includes('iot') || key.includes('temp') || key.includes('sensor')) return '🌡️';
  if (key.includes('diagnosis') || key.includes('disease') || key.includes('scan')) return '🔬';
  if (key.includes('treatment') || key.includes('medicine')) return '💊';
  if (key.includes('stock') || key.includes('inventory') || key.includes('product')) return '📦';
  if (key.includes('forum') || key.includes('community') || key.includes('post')) return '💬';
  return TOPIC_ICON[topicOrType] ?? '🔔';
}

function timeAgo(iso?: string): string {
  if (!iso) return 'recently';
  try {
    const diff = Date.now() - new Date(iso).getTime();
    if (isNaN(diff)) return 'recently';
    const mins = Math.floor(diff / 60_000);
    if (mins < 1)  return 'just now';
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24)  return `${hrs}h ago`;
    return `${Math.floor(hrs / 24)}d ago`;
  } catch {
    return 'recently';
  }
}

/* ─── Component ──────────────────────────────────────────────────── */
export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logoutUser } = useAuth();
  const { toggleNav } = useShell();
  const navigate = useNavigate();

  const [notesOpen, setNotesOpen]         = useState(false);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [notifsLoading, setNotifsLoading] = useState(false);
  const [notifsError, setNotifsError]     = useState<string | null>(null);

  const searchRef   = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const initial   = (user?.fullName || user?.name || 'U').trim().charAt(0).toUpperCase();
  const roleLabel = user?.role === 'BUYER' ? 'Buyer' : user?.role === 'ADMIN' ? 'Admin' : 'Farmer';

  /* ── Resolve the userId to use for notifications ─────────────── */
  // Backend stores notifications by userId which can be user.id, user.farmerId, or user.buyerId
  const userId = user?.farmerId || user?.buyerId || user?.id || '';

  /* ── Fetch notifications via API Gateway ─────────────────────── */
  const fetchNotifications = useCallback(async () => {
    if (!userId) {
      setNotifications([]);
      return;
    }
    setNotifsLoading(true);
    setNotifsError(null);
    try {
      const data = await getNotificationHistory(userId);
      setNotifications(Array.isArray(data) ? data : []);
    } catch (err: any) {
      const status = err?.response?.status;
      if (status === 404 || status === 204) {
        setNotifications([]);
      } else if (status === 401) {
        setNotifsError('Session expired. Please log in again.');
      } else {
        // Fallback gracefully so notifications drop down shows clean empty state
        setNotifications([]);
      }
    } finally {
      setNotifsLoading(false);
    }
  }, [userId]);

  /* ── Open/close the dropdown ─────────────────────────────────── */
  const toggleDropdown = () => {
    setNotesOpen((prev) => {
      const opening = !prev;
      if (opening) fetchNotifications();
      return opening;
    });
  };

  /* ── Close dropdown on outside click ────────────────────────── */
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setNotesOpen(false);
      }
    };
    if (notesOpen) document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [notesOpen]);

  /* ── Keyboard shortcut ⌘K → focus search ────────────────────── */
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

  const badgeCount = notifications.length;

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
        </label>
      )}

      <div className="nav-actions">
        {isAuthenticated ? (
          <>
            {/* ── Notification bell ─────────────────────────────── */}
            <div className="nav-bell-wrap" ref={dropdownRef}>
              <button
                type="button"
                className="nav-icon-btn"
                aria-label="Notifications"
                aria-expanded={notesOpen}
                onClick={toggleDropdown}
              >
                <IconBell />
                {badgeCount > 0 && (
                  <span className="nav-count">{badgeCount > 99 ? '99+' : badgeCount}</span>
                )}
              </button>

              {notesOpen && (
                <div className="nav-dropdown notif-dropdown">
                  {/* Header */}
                  <div className="notif-header">
                    <strong>Notifications</strong>
                    {notifications.length > 0 && (
                      <span className="notif-meta">{notifications.length} total</span>
                    )}
                  </div>

                  {/* States */}
                  {notifsLoading && (
                    <p className="notif-state">Loading…</p>
                  )}

                  {!notifsLoading && notifsError && (
                    <p className="notif-state notif-error">⚠️ {notifsError}</p>
                  )}

                  {!notifsLoading && !notifsError && notifications.length === 0 && (
                    <p className="notif-state">You have no notifications.</p>
                  )}

                  {/* Notification list */}
                  {!notifsLoading && !notifsError && notifications.length > 0 && (
                    <ul className="notif-list">
                      {notifications.map((n) => {
                        const isUnread = n.read === false || (n.status && n.status !== 'DELIVERED');
                        const iconKey = n.type || n.sourceTopic;
                        const dateStr = n.createdAt || n.sentAt;
                        return (
                          <li key={n.id} className={`notif-item${isUnread ? ' notif-unread' : ''}`}>
                            <span className="notif-icon">{topicIcon(iconKey)}</span>
                            <div className="notif-body">
                              <p className="notif-msg">{n.message}</p>
                              <span className="notif-time">{timeAgo(dateStr)}</span>
                            </div>
                          </li>
                        );
                      })}
                    </ul>
                  )}

                  {/* Footer */}
                  {!notifsLoading && (
                    <button
                      type="button"
                      className="notif-refresh"
                      onClick={fetchNotifications}
                      disabled={notifsLoading}
                    >
                      ↻ Refresh
                    </button>
                  )}
                </div>
              )}
            </div>

            {/* ── User chip ─────────────────────────────────────── */}
            <div className="user-chip">
              <div className="user-avatar">{initial}</div>
              <div className="user-meta">
                <strong>{user?.fullName || user?.name}</strong>
                <span>{roleLabel}</span>
              </div>
            </div>

            <button
              type="button"
              className="nav-text-btn"
              onClick={() => { logoutUser(); navigate('/login'); }}
            >
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
