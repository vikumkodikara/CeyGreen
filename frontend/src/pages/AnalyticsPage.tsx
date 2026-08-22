import React, { useState } from 'react';
import { getSalesSummary, getSalesTrend, getLeaderboard } from '../api/analytics';
import { Spinner } from '../components/ui/Spinner';
import { useAuth } from '../hooks/useAuth';
import { LeaderboardResponse, SalesSummary, SalesTrend } from '../types/analytics';

// ─── Grafana base URL ──────────────────────────────────────────────────────────
const GRAFANA_BASE = import.meta.env.VITE_GRAFANA_URL || 'http://localhost:3001';
const GRAFANA_DASHBOARD_UID = 'ceygreen-sales-analytics';

// Use /d-solo/ so Grafana renders only that one panel — no full-dashboard scroll
function grafanaPanelUrl(panelId: number, farmerId: string) {
  const params = new URLSearchParams({
    orgId: '1',
    panelId: String(panelId),
    'var-farmer': farmerId,
    from: 'now-90d',
    to: 'now',
    theme: 'dark',
    refresh: '30s',
  });
  return `${GRAFANA_BASE}/d-solo/${GRAFANA_DASHBOARD_UID}/sales-analytics?${params}`;
}

// ─── Static styles ─────────────────────────────────────────────────────────────
const S = {
  page: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #134e25 100%)',
    padding: '0 0 4rem',
    fontFamily: "'Inter', system-ui, sans-serif",
  } as React.CSSProperties,

  hero: {
    background: 'linear-gradient(120deg, rgba(22,163,74,0.18) 0%, rgba(15,23,42,0.95) 60%)',
    borderBottom: '1px solid rgba(255,255,255,0.06)',
    padding: '2.5rem 2rem 2rem',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    gap: '1rem',
  } as React.CSSProperties,

  heroBadge: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '0.4rem',
    background: 'rgba(22,163,74,0.15)',
    border: '1px solid rgba(22,163,74,0.35)',
    borderRadius: '100px',
    padding: '0.25rem 0.875rem',
    fontSize: '0.75rem',
    fontWeight: 600,
    color: '#4ade80',
    letterSpacing: '0.04em',
    textTransform: 'uppercase',
    marginBottom: '0.75rem',
  } as React.CSSProperties,

  heroTitle: {
    margin: 0,
    fontSize: 'clamp(1.6rem, 4vw, 2.5rem)',
    fontWeight: 800,
    color: '#f1f5f9',
    lineHeight: 1.15,
  } as React.CSSProperties,

  heroSubtitle: {
    margin: '0.5rem 0 0',
    color: '#94a3b8',
    fontSize: '1rem',
    maxWidth: '520px',
  } as React.CSSProperties,

  grafanaLink: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '0.5rem',
    background: 'rgba(249,115,22,0.12)',
    border: '1px solid rgba(249,115,22,0.4)',
    borderRadius: '0.6rem',
    padding: '0.5rem 1.1rem',
    color: '#fb923c',
    fontWeight: 600,
    fontSize: '0.875rem',
    textDecoration: 'none',
    whiteSpace: 'nowrap',
  } as React.CSSProperties,

  content: {
    maxWidth: '1300px',
    margin: '0 auto',
    padding: '2rem 1.5rem',
  } as React.CSSProperties,

  lookupCard: {
    background: 'rgba(255,255,255,0.04)',
    backdropFilter: 'blur(12px)',
    border: '1px solid rgba(255,255,255,0.09)',
    borderRadius: '1.25rem',
    padding: '1.75rem 2rem',
    marginBottom: '1.5rem',
  } as React.CSSProperties,

  lookupLabel: {
    display: 'block',
    fontSize: '0.78rem',
    fontWeight: 600,
    color: '#64748b',
    textTransform: 'uppercase',
    letterSpacing: '0.08em',
    marginBottom: '0.6rem',
  } as React.CSSProperties,

  lookupRow: {
    display: 'flex',
    gap: '0.75rem',
    alignItems: 'stretch',
  } as React.CSSProperties,

  lookupInput: {
    flex: 1,
    background: 'rgba(255,255,255,0.07)',
    border: '1px solid rgba(255,255,255,0.12)',
    borderRadius: '0.75rem',
    padding: '0.75rem 1.1rem',
    color: '#f1f5f9',
    fontSize: '1rem',
    outline: 'none',
    fontFamily: 'inherit',
  } as React.CSSProperties,

  fetchBtn: {
    background: 'linear-gradient(135deg, #16a34a 0%, #15803d 100%)',
    border: 'none',
    borderRadius: '0.75rem',
    padding: '0.75rem 1.75rem',
    color: '#fff',
    fontWeight: 700,
    fontSize: '0.95rem',
    cursor: 'pointer',
    letterSpacing: '0.02em',
    display: 'flex',
    alignItems: 'center',
    gap: '0.4rem',
    whiteSpace: 'nowrap',
  } as React.CSSProperties,

  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '1rem',
    marginBottom: '1.5rem',
  } as React.CSSProperties,

  sectionHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: '1rem',
  } as React.CSSProperties,

  sectionTitle: {
    fontSize: '1rem',
    fontWeight: 700,
    color: '#e2e8f0',
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    margin: 0,
  } as React.CSSProperties,

  grafanaGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(500px, 1fr))',
    gap: '1.25rem',
    marginBottom: '1.5rem',
  } as React.CSSProperties,

  grafanaCard: {
    background: '#161b27',
    border: '1px solid rgba(255,255,255,0.08)',
    borderRadius: '1.1rem',
    overflow: 'hidden',
  } as React.CSSProperties,

  grafanaFrame: {
    width: '100%',
    border: 'none',
    display: 'block',
  } as React.CSSProperties,

  tableWrap: {
    overflowX: 'auto',
    borderRadius: '1.1rem',
    border: '1px solid rgba(255,255,255,0.07)',
  } as React.CSSProperties,

  table: {
    width: '100%',
    borderCollapse: 'collapse',
    fontSize: '0.875rem',
  } as React.CSSProperties,

  th: {
    padding: '0.875rem 1rem',
    textAlign: 'left',
    fontWeight: 600,
    fontSize: '0.73rem',
    color: '#64748b',
    textTransform: 'uppercase',
    letterSpacing: '0.07em',
    borderBottom: '1px solid rgba(255,255,255,0.07)',
    background: 'rgba(0,0,0,0.2)',
    whiteSpace: 'nowrap',
  } as React.CSSProperties,

  td: {
    padding: '0.8rem 1rem',
    color: '#cbd5e1',
    borderBottom: '1px solid rgba(255,255,255,0.04)',
  } as React.CSSProperties,

  loadingWrap: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '1rem',
    padding: '4rem 0',
    color: '#64748b',
  } as React.CSSProperties,
};

// ─── Style helpers (functions — NOT in the styles record) ──────────────────────
function statCardStyle(accent: string): React.CSSProperties {
  return {
    background: `linear-gradient(135deg, rgba(${accent},0.14) 0%, rgba(${accent},0.04) 100%)`,
    border: `1px solid rgba(${accent},0.22)`,
    borderRadius: '1.1rem',
    padding: '1.4rem 1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '0.4rem',
  };
}

function statValueStyle(color: string): React.CSSProperties {
  return { fontSize: '1.75rem', fontWeight: 800, color, lineHeight: 1.1 };
}

function noticeStyle(type: 'info' | 'warn'): React.CSSProperties {
  return {
    display: 'flex',
    alignItems: 'center',
    gap: '0.625rem',
    padding: '0.875rem 1.25rem',
    borderRadius: '0.875rem',
    marginBottom: '1.25rem',
    fontSize: '0.875rem',
    background: type === 'info' ? 'rgba(22,163,74,0.1)' : 'rgba(245,158,11,0.1)',
    border: `1px solid ${type === 'info' ? 'rgba(22,163,74,0.3)' : 'rgba(245,158,11,0.3)'}`,
    color: type === 'info' ? '#4ade80' : '#fbbf24',
  };
}

function badgeStyle(status: string): React.CSSProperties {
  const map: Record<string, [string, string]> = {
    COMPLETED: ['rgba(22,163,74,0.15)',  '#4ade80'],
    PENDING:   ['rgba(245,158,11,0.15)', '#fbbf24'],
    CANCELLED: ['rgba(239,68,68,0.15)',  '#f87171'],
  };
  const [bg, color] = map[status] ?? ['rgba(100,116,139,0.15)', '#94a3b8'];
  return {
    display: 'inline-block',
    padding: '0.2rem 0.65rem',
    borderRadius: '100px',
    fontSize: '0.72rem',
    fontWeight: 700,
    letterSpacing: '0.04em',
    textTransform: 'uppercase',
    background: bg,
    color,
  };
}

// ─── Mock data ─────────────────────────────────────────────────────────────────
interface OrderLog {
  id: string; cropName: string; quantity: number;
  totalAmount: number; buyerName: string; status: string; date: string;
}

const MOCK_LOGS: OrderLog[] = [
  { id: 'ORD-8921', cropName: 'Organic Bell Peppers',    quantity: 15, totalAmount: 1850, buyerName: 'Green Mart Colombo',    status: 'COMPLETED', date: new Date(Date.now() - 7200000).toISOString()   },
  { id: 'ORD-8914', cropName: 'Hydroponic Tomatoes',     quantity: 20, totalAmount: 2100, buyerName: 'Fresh Organics Kandy', status: 'COMPLETED', date: new Date(Date.now() - 93600000).toISOString()  },
  { id: 'ORD-8898', cropName: 'Ceylon Cinnamon Sprouts', quantity:  8, totalAmount:  900, buyerName: 'Lanka Agro Exports',   status: 'COMPLETED', date: new Date(Date.now() - 187200000).toISOString() },
];

const MOCK_BOARD: LeaderboardResponse = [
  { rank: 1, farmerId: 'FARMER-101', totalRevenue: 148500, totalOrders: 24, lastUpdated: new Date().toISOString() },
  { rank: 2, farmerId: 'FARMER-204', totalRevenue: 112300, totalOrders: 19, lastUpdated: new Date().toISOString() },
  { rank: 3, farmerId: 'FARMER-088', totalRevenue:  87200, totalOrders: 14, lastUpdated: new Date().toISOString() },
];

// ─── Helpers ───────────────────────────────────────────────────────────────────
const fmtCurrency = (n: number) =>
  new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR', maximumFractionDigits: 2 }).format(n || 0);

const fmtDate = (iso?: string) => {
  if (!iso) return '—';
  try {
    const d = new Date(iso);
    return isNaN(d.getTime()) ? iso : d.toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' });
  } catch { return iso; }
};

// ─── Component ─────────────────────────────────────────────────────────────────
export const AnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const [farmerId,    setFarmerId]    = useState(user?.farmerId || 'FARMER-101');
  const [activeFid,   setActiveFid]   = useState<string | null>(null);
  const [summary,     setSummary]     = useState<SalesSummary | null>(null);
  const [trend,       setTrend]       = useState<SalesTrend | null>(null);
  const [logs,        setLogs]        = useState<OrderLog[]>([]);
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const [loading,     setLoading]     = useState(false);
  const [isMock,      setIsMock]      = useState(false);
  const [notice,      setNotice]      = useState<string | null>(null);

  const fetchAnalytics = async () => {
    const fid = farmerId.trim() || 'FARMER-101';
    setLoading(true); setNotice(null); setIsMock(false);
    setSummary(null); setTrend(null); setLogs([]); setLeaderboard(null); setActiveFid(null);

    let summaryData: SalesSummary | null = null;
    let trendData:   SalesTrend   | null = null;
    let boardData:   LeaderboardResponse | null = null;
    let useMock = false;

    try { summaryData = await getSalesSummary(fid); } catch { useMock = true; }

    if (!useMock) {
      try {
        trendData = await getSalesTrend(fid);
        if (trendData?.orderHistory?.length) {
          setLogs(trendData.orderHistory.map((h) => ({
            id:          String(h.orderId ?? h.id ?? '—'),
            cropName:    h.product ?? 'Fresh Produce',
            quantity:    10,
            totalAmount: Number(h.amount) || 0,
            buyerName:   'Direct Buyer',
            status:      'COMPLETED',
            date:        h.recordedAt,
          })));
        }
      } catch { /* optional */ }
    }

    try { boardData = await getLeaderboard(); } catch { boardData = MOCK_BOARD; }

    if (useMock || !summaryData) {
      setIsMock(true);
      setNotice('Analytics backend offline — showing mock data. Start Docker containers to load live data.');
      summaryData = { farmerId: fid, totalOrders: 24, totalRevenue: 148500, lastUpdated: new Date().toISOString() };
      setLogs(MOCK_LOGS);
      boardData = MOCK_BOARD;
    }

    setSummary(summaryData);
    setTrend(trendData);
    setLeaderboard(boardData);
    setActiveFid(fid);
    setLoading(false);
  };

  const avgOrderValue = summary && summary.totalOrders > 0
    ? summary.totalRevenue / summary.totalOrders : 0;

  return (
    <div style={S.page}>
      {/* ── Hero ── */}
      <header style={S.hero}>
        <div>
          <div style={S.heroBadge}><span>📊</span> Sales Intelligence</div>
          <h1 style={S.heroTitle}>Sales Analytics</h1>
          <p style={S.heroSubtitle}>
            Live revenue, order summaries, and activity logs pulled from PostgreSQL —
            visualised with Grafana.
          </p>
        </div>
        <a
          href={`${GRAFANA_BASE}/d/${GRAFANA_DASHBOARD_UID}`}
          target="_blank" rel="noopener noreferrer"
          style={S.grafanaLink}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2">
            <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
            <polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/>
          </svg>
          Open in Grafana
        </a>
      </header>

      <div style={S.content}>
        {/* ── Lookup ── */}
        <div style={S.lookupCard}>
          <label style={S.lookupLabel}>Look up a farmer</label>
          <div style={S.lookupRow}>
            <input
              style={S.lookupInput}
              value={farmerId}
              onChange={(e) => setFarmerId(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && fetchAnalytics()}
              placeholder="e.g. FARMER-101 or UUID"
            />
            <button
              style={{ ...S.fetchBtn, opacity: loading ? 0.65 : 1 }}
              onClick={fetchAnalytics}
              disabled={loading}
            >
              {loading ? <Spinner /> : '⚡'} Fetch
            </button>
          </div>
        </div>

        {/* ── Notice ── */}
        {notice && (
          <div style={noticeStyle(isMock ? 'warn' : 'info')}>
            <span>{isMock ? '⚠️' : '🌱'}</span>
            <span>{notice}</span>
          </div>
        )}

        {/* ── Loading ── */}
        {loading && (
          <div style={S.loadingWrap}>
            <Spinner />
            <span>Fetching sales analytics for {farmerId}…</span>
          </div>
        )}

        {/* ── Results ── */}
        {!loading && summary && activeFid && (
          <>
            {/* KPI stat cards */}
            <div style={S.statsGrid}>
              <div style={statCardStyle('22,163,74')}>
                <span style={S.lookupLabel}>Total Orders</span>
                <span style={statValueStyle('#4ade80')}>{summary.totalOrders.toLocaleString()}</span>
                <span style={{ fontSize: '0.8rem', color: '#475569' }}>lifetime orders</span>
              </div>
              <div style={statCardStyle('14,165,233')}>
                <span style={S.lookupLabel}>Total Revenue</span>
                <span style={statValueStyle('#38bdf8')}>{fmtCurrency(summary.totalRevenue)}</span>
                <span style={{ fontSize: '0.8rem', color: '#475569' }}>all-time gross</span>
              </div>
              <div style={statCardStyle('168,85,247')}>
                <span style={S.lookupLabel}>Avg Order Value</span>
                <span style={statValueStyle('#c084fc')}>{fmtCurrency(avgOrderValue)}</span>
                <span style={{ fontSize: '0.8rem', color: '#475569' }}>per transaction</span>
              </div>
              <div style={statCardStyle('249,115,22')}>
                <span style={S.lookupLabel}>Last Updated</span>
                <span style={{ ...statValueStyle('#fb923c'), fontSize: '1.1rem' }}>{fmtDate(summary.lastUpdated)}</span>
                <span style={{ fontSize: '0.8rem', color: '#475569' }}>latest sync</span>
              </div>
            </div>

            {/* ── Grafana embedded panels ── */}
            {!isMock && (
              <>
                <div style={S.sectionHeader}>
                  <h2 style={S.sectionTitle}>
                    <span>📈</span> Charts — Powered by Grafana + PostgreSQL
                  </h2>
                  <span style={{ fontSize: '0.78rem', color: '#475569' }}>auto-refreshes every 30s</span>
                </div>

                <div style={S.grafanaGrid}>
                  <div style={S.grafanaCard}>
                    <iframe
                      src={grafanaPanelUrl(5, activeFid)}
                      style={{ ...S.grafanaFrame, height: '400px' }}
                      title="Revenue by Product"
                      allowFullScreen
                    />
                  </div>
                  <div style={S.grafanaCard}>
                    <iframe
                      src={grafanaPanelUrl(6, activeFid)}
                      style={{ ...S.grafanaFrame, height: '400px' }}
                      title="Orders by Product"
                      allowFullScreen
                    />
                  </div>
                </div>

                <div style={{ ...S.grafanaCard, marginBottom: '1.5rem' }}>
                  <iframe
                    src={grafanaPanelUrl(7, activeFid)}
                    style={{ ...S.grafanaFrame, height: '380px' }}
                    title="Order Activity Timeline"
                    allowFullScreen
                  />
                </div>
              </>
            )}

            {/* ── Recent Order Log ── */}
            <div style={S.sectionHeader}>
              <h2 style={S.sectionTitle}><span>🧾</span> Recent Activity &amp; Order Log</h2>
              {isMock && <span style={{ fontSize: '0.78rem', color: '#fbbf24' }}>mock data</span>}
            </div>
            <div style={{ ...S.tableWrap, marginBottom: '1.5rem' }}>
              {logs.length > 0 ? (
                <table style={S.table}>
                  <thead>
                    <tr>
                      {['Order ID', 'Product / Crop', 'Qty', 'Total Amount', 'Buyer', 'Status', 'Recorded At'].map((h) => (
                        <th key={h} style={S.th}>{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {logs.map((log) => (
                      <tr key={log.id}>
                        <td style={{ ...S.td, fontWeight: 700, color: '#e2e8f0', fontFamily: 'monospace' }}>#{log.id}</td>
                        <td style={S.td}>{log.cropName}</td>
                        <td style={{ ...S.td, color: '#94a3b8' }}>{log.quantity} units</td>
                        <td style={{ ...S.td, fontWeight: 700, color: '#38bdf8' }}>{fmtCurrency(log.totalAmount)}</td>
                        <td style={S.td}>{log.buyerName}</td>
                        <td style={S.td}><span style={badgeStyle(log.status)}>{log.status}</span></td>
                        <td style={{ ...S.td, fontSize: '0.8rem', color: '#475569' }}>{fmtDate(log.date)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div style={{ padding: '2.5rem', textAlign: 'center', color: '#475569' }}>
                  No order logs found for <strong style={{ color: '#94a3b8' }}>{activeFid}</strong>.
                </div>
              )}
            </div>

            {/* ── Leaderboard ── */}
            {leaderboard && leaderboard.length > 0 && (
              <>
                <div style={S.sectionHeader}>
                  <h2 style={S.sectionTitle}><span>🏆</span> Revenue Leaderboard</h2>
                </div>
                <div style={S.tableWrap}>
                  <table style={S.table}>
                    <thead>
                      <tr>
                        {['Rank', 'Farmer ID', 'Total Revenue', 'Total Orders', 'Last Updated'].map((h) => (
                          <th key={h} style={S.th}>{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {leaderboard.map((e) => {
                        const isActive = e.farmerId === activeFid;
                        return (
                          <tr key={e.farmerId} style={{ background: isActive ? 'rgba(22,163,74,0.07)' : 'transparent' }}>
                            <td style={{ ...S.td, fontWeight: 800, fontSize: '1.1rem' }}>
                              {e.rank === 1 ? '🥇' : e.rank === 2 ? '🥈' : e.rank === 3 ? '🥉' : `#${e.rank}`}
                            </td>
                            <td style={{ ...S.td, fontWeight: isActive ? 700 : 400, color: isActive ? '#4ade80' : '#e2e8f0', fontFamily: 'monospace' }}>
                              {e.farmerId}
                              {isActive && (
                                <span style={{ marginLeft: '0.5rem', fontSize: '0.7rem', color: '#4ade80', background: 'rgba(22,163,74,0.15)', padding: '0.1rem 0.5rem', borderRadius: '100px' }}>
                                  you
                                </span>
                              )}
                            </td>
                            <td style={{ ...S.td, fontWeight: 700, color: '#38bdf8' }}>{fmtCurrency(e.totalRevenue)}</td>
                            <td style={S.td}>{e.totalOrders.toLocaleString()}</td>
                            <td style={{ ...S.td, fontSize: '0.8rem', color: '#475569' }}>{fmtDate(e.lastUpdated)}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
};
