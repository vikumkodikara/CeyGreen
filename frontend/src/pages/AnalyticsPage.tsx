import React, { useState, useEffect, useMemo } from 'react';
import { getSalesSummary, getSalesTrend } from '../api/analytics';
import { getMyOrders, getFarmerOrders } from '../api/orderApi';
import { Spinner } from '../components/ui/Spinner';
import { useAuth } from '../hooks/useAuth';
import { SalesSummary, SalesTrend } from '../types/analytics';
import { Order } from '../types/order';
import './AnalyticsPage.css';

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

const statusColor: Record<string, { bg: string; color: string }> = {
  PENDING:   { bg: '#fef9c3', color: '#854d0e' },
  CONFIRMED: { bg: '#dbeafe', color: '#1e40af' },
  SHIPPED:   { bg: '#e0e7ff', color: '#4338ca' },
  DELIVERED: { bg: '#dcfce7', color: '#15803d' },
  CANCELLED: { bg: '#fee2e2', color: '#b91c1c' },
  COMPLETED: { bg: '#dcfce7', color: '#15803d' },
};

type TabKey = 'overview' | 'selling' | 'buying';

// ─── Component ─────────────────────────────────────────────────────────────────
export const AnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const farmerId = user?.farmerId || user?.id || '';
  const userRole = user?.role || 'FARMER';

  const [activeTab, setActiveTab] = useState<TabKey>('overview');

  // Selling data
  const [summary, setSummary] = useState<SalesSummary | null>(null);
  const [trend, setTrend] = useState<SalesTrend | null>(null);
  const [sellingOrders, setSellingOrders] = useState<Order[]>([]);
  const [sellingLoading, setSellingLoading] = useState(true);

  // Buying data
  const [buyingOrders, setBuyingOrders] = useState<Order[]>([]);
  const [buyingLoading, setBuyingLoading] = useState(true);

  // Notice
  const [notice, setNotice] = useState<string | null>(null);
  const [isMock, setIsMock] = useState(false);

  // ── Fetch selling analytics ──────────────────────────────────────────────────
  useEffect(() => {
    if (!farmerId) return;
    setSellingLoading(true);
    setNotice(null);
    setIsMock(false);

    const loadSelling = async () => {
      let summaryData: SalesSummary | null = null;
      let trendData: SalesTrend | null = null;
      let orders: Order[] = [];
      let useMock = false;

      try { summaryData = await getSalesSummary(farmerId); } catch { useMock = true; }
      if (!useMock) {
        try { trendData = await getSalesTrend(farmerId); } catch { /* optional */ }
      }

      try {
        const page = await getFarmerOrders({ page: 0, size: 50 });
        orders = page.content;
      } catch { /* optional */ }

      // If backend is offline, use data from orders or show mock notice
      if (useMock || !summaryData) {
        setIsMock(true);
        // Build summary from actual orders if available
        if (orders.length > 0) {
          const totalRevenue = orders.reduce((sum, o) => sum + (o.totalPrice || 0), 0);
          summaryData = {
            farmerId,
            totalOrders: orders.length,
            totalRevenue,
            lastUpdated: new Date().toISOString(),
          };
        } else {
          setNotice('Analytics backend offline — showing placeholder data. Start the backend to load live data.');
          summaryData = { farmerId, totalOrders: 0, totalRevenue: 0, lastUpdated: new Date().toISOString() };
        }
      }

      setSummary(summaryData);
      setTrend(trendData);
      setSellingOrders(orders);
      setSellingLoading(false);
    };

    loadSelling();
  }, [farmerId]);

  // ── Fetch buying analytics ───────────────────────────────────────────────────
  useEffect(() => {
    setBuyingLoading(true);

    const loadBuying = async () => {
      try {
        const page = await getMyOrders({ page: 0, size: 50 });
        setBuyingOrders(page.content);
      } catch {
        setBuyingOrders([]);
      }
      setBuyingLoading(false);
    };

    loadBuying();
  }, []);

  // ── Computed stats ───────────────────────────────────────────────────────────
  const sellingStats = useMemo(() => {
    const totalRevenue = summary?.totalRevenue || sellingOrders.reduce((s, o) => s + (o.totalPrice || 0), 0);
    const totalOrders = summary?.totalOrders || sellingOrders.length;
    const avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;
    const pendingCount = sellingOrders.filter(o => o.status === 'PENDING').length;
    const deliveredCount = sellingOrders.filter(o => o.status === 'DELIVERED').length;
    const cancelledCount = sellingOrders.filter(o => o.status === 'CANCELLED').length;
    return { totalRevenue, totalOrders, avgOrder, pendingCount, deliveredCount, cancelledCount };
  }, [summary, sellingOrders]);

  const buyingStats = useMemo(() => {
    const totalSpent = buyingOrders.reduce((s, o) => s + (o.totalPrice || 0), 0);
    const totalOrders = buyingOrders.length;
    const avgOrder = totalOrders > 0 ? totalSpent / totalOrders : 0;
    const pendingCount = buyingOrders.filter(o => o.status === 'PENDING').length;
    const deliveredCount = buyingOrders.filter(o => o.status === 'DELIVERED').length;
    const cancelledCount = buyingOrders.filter(o => o.status === 'CANCELLED').length;
    return { totalSpent, totalOrders, avgOrder, pendingCount, deliveredCount, cancelledCount };
  }, [buyingOrders]);

  const overviewStats = useMemo(() => ({
    totalTransactions: sellingStats.totalOrders + buyingStats.totalOrders,
    netRevenue: sellingStats.totalRevenue - buyingStats.totalSpent,
    sellingRevenue: sellingStats.totalRevenue,
    totalSpent: buyingStats.totalSpent,
  }), [sellingStats, buyingStats]);

  // ── Tabs config ──────────────────────────────────────────────────────────────
  const tabs: { key: TabKey; icon: string; label: string }[] = [
    { key: 'overview', icon: '📊', label: 'Overview' },
    { key: 'selling', icon: '🌾', label: 'My Sales' },
    { key: 'buying', icon: '🛒', label: 'My Purchases' },
  ];

  const isLoading = sellingLoading || buyingLoading;

  return (
    <div className="analytics-page">
      {/* ── Hero ── */}
      <header className="analytics-hero">
        <div>
          <div className="analytics-badge">
            <span>📊</span> My E-Commerce Analytics
          </div>
          <h1 className="analytics-title">
            {user?.name ? `${user.name}'s Analytics` : 'My Analytics'}
          </h1>
          <p className="analytics-subtitle">
            Track your selling and buying performance on CeyGreen Marketplace.
          </p>
        </div>
        <div className="analytics-user-chip">
          <span className="analytics-user-avatar">
            {(user?.name || 'U').charAt(0).toUpperCase()}
          </span>
          <div>
            <div className="analytics-user-name">{user?.name || 'User'}</div>
            <div className="analytics-user-role">{userRole}</div>
          </div>
        </div>
      </header>

      <div className="analytics-content">
        {/* ── Tabs ── */}
        <div className="analytics-tabs">
          {tabs.map(t => (
            <button
              key={t.key}
              className={`analytics-tab ${activeTab === t.key ? 'active' : ''}`}
              onClick={() => setActiveTab(t.key)}
            >
              <span>{t.icon}</span> {t.label}
            </button>
          ))}
        </div>

        {/* ── Notice ── */}
        {notice && (
          <div className={`analytics-notice ${isMock ? 'warn' : 'info'}`}>
            <span>{isMock ? '⚠️' : '🌱'}</span>
            <span>{notice}</span>
          </div>
        )}

        {/* ── Loading ── */}
        {isLoading && (
          <div className="analytics-loading">
            <Spinner />
            <span>Loading your analytics…</span>
          </div>
        )}

        {/* ── Overview Tab ── */}
        {!isLoading && activeTab === 'overview' && (
          <>
            <div className="analytics-stats-grid four-col">
              <div className="stat-card stat-green">
                <span className="stat-label">Total Transactions</span>
                <span className="stat-value green">{overviewStats.totalTransactions.toLocaleString()}</span>
                <span className="stat-desc">all sales + purchases</span>
              </div>
              <div className="stat-card stat-emerald">
                <span className="stat-label">Selling Revenue</span>
                <span className="stat-value emerald">{fmtCurrency(overviewStats.sellingRevenue)}</span>
                <span className="stat-desc">total income from sales</span>
              </div>
              <div className="stat-card stat-blue">
                <span className="stat-label">Total Spent</span>
                <span className="stat-value blue">{fmtCurrency(overviewStats.totalSpent)}</span>
                <span className="stat-desc">total purchases made</span>
              </div>
              <div className="stat-card stat-teal">
                <span className="stat-label">Net Balance</span>
                <span className={`stat-value ${overviewStats.netRevenue >= 0 ? 'teal' : 'red'}`}>
                  {fmtCurrency(overviewStats.netRevenue)}
                </span>
                <span className="stat-desc">revenue − spending</span>
              </div>
            </div>

            {/* Order Status Breakdown */}
            <div className="analytics-section">
              <h2 className="analytics-section-title">
                <span>📋</span> Order Status Breakdown
              </h2>
              <div className="analytics-stats-grid three-col">
                <div className="stat-card stat-outline">
                  <span className="stat-label">Selling Orders</span>
                  <div className="stat-breakdown">
                    <div className="breakdown-item">
                      <span className="breakdown-dot pending" />
                      <span>Pending</span>
                      <span className="breakdown-val">{sellingStats.pendingCount}</span>
                    </div>
                    <div className="breakdown-item">
                      <span className="breakdown-dot delivered" />
                      <span>Delivered</span>
                      <span className="breakdown-val">{sellingStats.deliveredCount}</span>
                    </div>
                    <div className="breakdown-item">
                      <span className="breakdown-dot cancelled" />
                      <span>Cancelled</span>
                      <span className="breakdown-val">{sellingStats.cancelledCount}</span>
                    </div>
                  </div>
                </div>
                <div className="stat-card stat-outline">
                  <span className="stat-label">Buying Orders</span>
                  <div className="stat-breakdown">
                    <div className="breakdown-item">
                      <span className="breakdown-dot pending" />
                      <span>Pending</span>
                      <span className="breakdown-val">{buyingStats.pendingCount}</span>
                    </div>
                    <div className="breakdown-item">
                      <span className="breakdown-dot delivered" />
                      <span>Delivered</span>
                      <span className="breakdown-val">{buyingStats.deliveredCount}</span>
                    </div>
                    <div className="breakdown-item">
                      <span className="breakdown-dot cancelled" />
                      <span>Cancelled</span>
                      <span className="breakdown-val">{buyingStats.cancelledCount}</span>
                    </div>
                  </div>
                </div>
                <div className="stat-card stat-outline">
                  <span className="stat-label">Averages</span>
                  <div className="stat-breakdown">
                    <div className="breakdown-item">
                      <span className="breakdown-dot teal-dot" />
                      <span>Avg Sale</span>
                      <span className="breakdown-val">{fmtCurrency(sellingStats.avgOrder)}</span>
                    </div>
                    <div className="breakdown-item">
                      <span className="breakdown-dot blue-dot" />
                      <span>Avg Purchase</span>
                      <span className="breakdown-val">{fmtCurrency(buyingStats.avgOrder)}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Recent Activity — last 5 of each */}
            <div className="analytics-section">
              <h2 className="analytics-section-title"><span>🔄</span> Recent Activity</h2>
              <div className="analytics-two-col">
                <div>
                  <h3 className="analytics-col-title">Recent Sales</h3>
                  {sellingOrders.length > 0 ? (
                    <div className="analytics-mini-list">
                      {sellingOrders.slice(0, 5).map(o => (
                        <div key={o.id} className="mini-list-item">
                          <div className="mini-list-left">
                            <span className="mini-order-id">#{o.id}</span>
                            <span className="mini-product">{o.cropName || `Product ${o.productId}`}</span>
                          </div>
                          <div className="mini-list-right">
                            <span className="mini-amount">{fmtCurrency(o.totalPrice)}</span>
                            <span className="mini-status" style={{ background: statusColor[o.status]?.bg, color: statusColor[o.status]?.color }}>
                              {o.status}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="analytics-empty">No sales yet</div>
                  )}
                </div>
                <div>
                  <h3 className="analytics-col-title">Recent Purchases</h3>
                  {buyingOrders.length > 0 ? (
                    <div className="analytics-mini-list">
                      {buyingOrders.slice(0, 5).map(o => (
                        <div key={o.id} className="mini-list-item">
                          <div className="mini-list-left">
                            <span className="mini-order-id">#{o.id}</span>
                            <span className="mini-product">{o.cropName || `Product ${o.productId}`}</span>
                          </div>
                          <div className="mini-list-right">
                            <span className="mini-amount">{fmtCurrency(o.totalPrice)}</span>
                            <span className="mini-status" style={{ background: statusColor[o.status]?.bg, color: statusColor[o.status]?.color }}>
                              {o.status}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="analytics-empty">No purchases yet</div>
                  )}
                </div>
              </div>
            </div>
          </>
        )}

        {/* ── Selling Tab ── */}
        {!isLoading && activeTab === 'selling' && (
          <>
            <div className="analytics-stats-grid four-col">
              <div className="stat-card stat-green">
                <span className="stat-label">Total Sales</span>
                <span className="stat-value green">{sellingStats.totalOrders.toLocaleString()}</span>
                <span className="stat-desc">orders received</span>
              </div>
              <div className="stat-card stat-emerald">
                <span className="stat-label">Total Revenue</span>
                <span className="stat-value emerald">{fmtCurrency(sellingStats.totalRevenue)}</span>
                <span className="stat-desc">gross income</span>
              </div>
              <div className="stat-card stat-teal">
                <span className="stat-label">Avg Order Value</span>
                <span className="stat-value teal">{fmtCurrency(sellingStats.avgOrder)}</span>
                <span className="stat-desc">per transaction</span>
              </div>
              <div className="stat-card stat-slate">
                <span className="stat-label">Last Updated</span>
                <span className="stat-value-sm slate">{fmtDate(summary?.lastUpdated)}</span>
                <span className="stat-desc">latest sync</span>
              </div>
            </div>

            {/* Selling Orders Table */}
            <div className="analytics-section">
              <div className="analytics-section-header">
                <h2 className="analytics-section-title"><span>🧾</span> My Sales Orders</h2>
                {isMock && <span className="mock-badge">demo data</span>}
              </div>
              <div className="analytics-table-wrap">
                {sellingOrders.length > 0 ? (
                  <table className="analytics-table">
                    <thead>
                      <tr>
                        {['Order ID', 'Product', 'Qty', 'Total', 'Buyer', 'Status', 'Date'].map(h => (
                          <th key={h}>{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {sellingOrders.map(o => (
                        <tr key={o.id}>
                          <td className="cell-mono">#{o.id}</td>
                          <td>{o.cropName || `Product ${o.productId}`}</td>
                          <td className="cell-muted">{o.quantity} units</td>
                          <td className="cell-green">{fmtCurrency(o.totalPrice)}</td>
                          <td>{o.buyerName || (o.buyerId ? o.buyerId.slice(0, 8) + '…' : '—')}</td>
                          <td>
                            <span className="status-pill" style={{ background: statusColor[o.status]?.bg, color: statusColor[o.status]?.color }}>
                              {o.status}
                            </span>
                          </td>
                          <td className="cell-date">{fmtDate(o.orderedAt)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <div className="analytics-empty-table">
                    No sales orders found. Start listing products on the marketplace to receive orders.
                  </div>
                )}
              </div>
            </div>
          </>
        )}

        {/* ── Buying Tab ── */}
        {!isLoading && activeTab === 'buying' && (
          <>
            <div className="analytics-stats-grid four-col">
              <div className="stat-card stat-blue">
                <span className="stat-label">Total Purchases</span>
                <span className="stat-value blue">{buyingStats.totalOrders.toLocaleString()}</span>
                <span className="stat-desc">orders placed</span>
              </div>
              <div className="stat-card stat-indigo">
                <span className="stat-label">Total Spent</span>
                <span className="stat-value indigo">{fmtCurrency(buyingStats.totalSpent)}</span>
                <span className="stat-desc">all-time spending</span>
              </div>
              <div className="stat-card stat-purple">
                <span className="stat-label">Avg Purchase</span>
                <span className="stat-value purple">{fmtCurrency(buyingStats.avgOrder)}</span>
                <span className="stat-desc">per order</span>
              </div>
              <div className="stat-card stat-slate">
                <span className="stat-label">Pending</span>
                <span className="stat-value slate">{buyingStats.pendingCount}</span>
                <span className="stat-desc">awaiting delivery</span>
              </div>
            </div>

            {/* Buying Orders Table */}
            <div className="analytics-section">
              <h2 className="analytics-section-title"><span>🛍️</span> My Purchase Orders</h2>
              <div className="analytics-table-wrap">
                {buyingOrders.length > 0 ? (
                  <table className="analytics-table">
                    <thead>
                      <tr>
                        {['Order ID', 'Product', 'Qty', 'Total', 'Status', 'Date'].map(h => (
                          <th key={h}>{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {buyingOrders.map(o => (
                        <tr key={o.id}>
                          <td className="cell-mono">#{o.id}</td>
                          <td>{o.cropName || `Product ${o.productId}`}</td>
                          <td className="cell-muted">{o.quantity} units</td>
                          <td className="cell-blue">{fmtCurrency(o.totalPrice)}</td>
                          <td>
                            <span className="status-pill" style={{ background: statusColor[o.status]?.bg, color: statusColor[o.status]?.color }}>
                              {o.status}
                            </span>
                          </td>
                          <td className="cell-date">{fmtDate(o.orderedAt)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <div className="analytics-empty-table">
                    No purchases found. Browse the marketplace to start buying.
                  </div>
                )}
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
