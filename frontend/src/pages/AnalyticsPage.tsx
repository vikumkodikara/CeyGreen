import React, { useState } from 'react';
import { getSalesSummary, getLeaderboard } from '../api/analytics';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useAuth } from '../hooks/useAuth';
import { LeaderboardResponse, SalesSummary } from '../types/analytics';
import { PageHeader } from '../components/layout/PageHeader';

export const AnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const [farmerId, setFarmerId] = useState(user?.farmerId || 'FARMER-101');
  const [summary, setSummary] = useState<SalesSummary | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [summaryError, setSummaryError] = useState<string | null>(null);
  const [leaderboardError, setLeaderboardError] = useState<string | null>(null);

  const fetchAnalytics = async () => {
    if (!farmerId.trim()) return;
    setLoading(true);
    setSummaryError(null);
    setLeaderboardError(null);
    setSummary(null);
    setLeaderboard(null);

    // Fetch sales summary for the entered farmer ID
    try {
      const s = await getSalesSummary(farmerId.trim());
      setSummary(s);
    } catch (err: any) {
      const status = err?.response?.status;
      if (status === 404) {
        setSummaryError(`No sales data found for farmer "${farmerId}".`);
      } else if (status === 401) {
        setSummaryError('Unauthorized: invalid or missing API key.');
      } else {
        setSummaryError('Failed to fetch sales summary. Make sure the service is running on port 8086.');
      }
    }

    // Fetch leaderboard
    try {
      const lb = await getLeaderboard();
      setLeaderboard(lb);
    } catch (err: any) {
      setLeaderboardError('Failed to fetch leaderboard data.');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR', maximumFractionDigits: 2 }).format(amount);

  const formatDate = (iso: string) =>
    new Date(iso).toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' });

  return (
    <div className="page-wrap">
      <PageHeader
        title="Sales Analytics"
        subtitle="Fetch revenue and order data for any farmer account from the CeyGreen Analytics Service."
      />

      {/* Search card */}
      <Card title="Look up a farmer">
        <div className="row">
          <div className="grow">
            <Input
              label="Farmer ID"
              value={farmerId}
              onChange={(e) => setFarmerId(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && fetchAnalytics()}
              placeholder="e.g. FARMER-101"
            />
          </div>
          <Button onClick={fetchAnalytics} isLoading={loading} style={{ marginBottom: '1rem' }}>
            Fetch
          </Button>
        </div>
      </Card>

      {/* Sales summary */}
      {summaryError && (
        <div
          style={{
            marginTop: '1.25rem',
            padding: '0.875rem 1.25rem',
            borderRadius: '0.5rem',
            background: '#fff1f0',
            border: '1px solid #ffa39e',
            color: '#cf1322',
            fontSize: '0.9rem',
          }}
        >
          ⚠️ {summaryError}
        </div>
      )}

      {summary && (
        <div className="dash-stats" style={{ marginTop: '1.25rem' }}>
          <div className="stat-tile">
            <div className="k">Farmer ID</div>
            <div className="v" style={{ fontSize: '1.05rem' }}>{summary.farmerId}</div>
          </div>
          <div className="stat-tile">
            <div className="k">Total Revenue</div>
            <div className="v">{formatCurrency(summary.totalRevenue)}</div>
          </div>
          <div className="stat-tile">
            <div className="k">Completed Orders</div>
            <div className="v">{summary.totalOrders.toLocaleString()}</div>
          </div>
          <div className="stat-tile">
            <div className="k">Last Updated</div>
            <div className="v" style={{ fontSize: '0.9rem' }}>{formatDate(summary.lastUpdated)}</div>
          </div>
        </div>
      )}

      {/* Leaderboard */}
      {leaderboardError && (
        <div
          style={{
            marginTop: '1.25rem',
            padding: '0.875rem 1.25rem',
            borderRadius: '0.5rem',
            background: '#fff1f0',
            border: '1px solid #ffa39e',
            color: '#cf1322',
            fontSize: '0.9rem',
          }}
        >
          ⚠️ {leaderboardError}
        </div>
      )}

      {leaderboard && leaderboard.length > 0 && (
        <Card title="Top Farmers — Revenue Leaderboard" style={{ marginTop: '1.25rem' }}>
          <div className="orders-table-wrap">
            <table className="marketplace-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Farmer ID</th>
                  <th>Total Revenue</th>
                  <th>Total Orders</th>
                  <th>Last Updated</th>
                </tr>
              </thead>
              <tbody>
                {leaderboard.map((entry) => (
                  <tr
                    key={entry.farmerId}
                    style={entry.farmerId === farmerId.trim() ? { background: '#f0f9ff', fontWeight: 600 } : {}}
                  >
                    <td>
                      {entry.rank === 1 ? '🥇' : entry.rank === 2 ? '🥈' : entry.rank === 3 ? '🥉' : `#${entry.rank}`}
                    </td>
                    <td>{entry.farmerId}</td>
                    <td>{formatCurrency(entry.totalRevenue)}</td>
                    <td>{entry.totalOrders.toLocaleString()}</td>
                    <td style={{ fontSize: '0.85rem', color: '#666' }}>{formatDate(entry.lastUpdated)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}
    </div>
  );
};
