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
  const [farmerId, setFarmerId] = useState(user?.farmerId || 'farmer-1');
  const [summary, setSummary] = useState<SalesSummary | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardResponse | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchAnalytics = async () => {
    setLoading(true);
    try {
      const s = await getSalesSummary(farmerId);
      setSummary(s);
    } catch {
      setSummary(null);
    }

    try {
      const lb = await getLeaderboard();
      setLeaderboard(lb);
    } catch {
      setLeaderboard(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrap">
      <PageHeader
        title="Analytics"
        subtitle="Revenue and orders for a farmer account."
      />

      <Card title="Look up a farmer">
        <div className="row">
          <div className="grow">
            <Input label="Farmer ID" value={farmerId} onChange={(e) => setFarmerId(e.target.value)} />
          </div>
          <Button onClick={fetchAnalytics} isLoading={loading} style={{ marginBottom: '1rem' }}>
            Fetch
          </Button>
        </div>
      </Card>

      {summary && (
        <div className="dash-stats" style={{ marginTop: '1.25rem' }}>
          <div className="stat-tile">
            <div className="k">Total revenue</div>
            <div className="v">${summary.totalRevenue}</div>
          </div>
          <div className="stat-tile">
            <div className="k">Completed orders</div>
            <div className="v">{summary.totalOrders}</div>
          </div>
          <div className="stat-tile">
            <div className="k">Farmer</div>
            <div className="v" style={{ fontSize: '1.05rem' }}>{farmerId}</div>
          </div>
        </div>
      )}

      {leaderboard && (
        <Card title="Top farmers" style={{ marginTop: '1.25rem' }}>
          <div className="orders-table-wrap">
            <table className="marketplace-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Farmer</th>
                  <th>Revenue</th>
                  <th>Orders</th>
                </tr>
              </thead>
              <tbody>
                {leaderboard.farmers.map((f) => (
                  <tr key={f.farmerId}>
                    <td>#{f.rank}</td>
                    <td>{f.farmerId}</td>
                    <td>${f.totalRevenue}</td>
                    <td>{f.totalOrders}</td>
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
