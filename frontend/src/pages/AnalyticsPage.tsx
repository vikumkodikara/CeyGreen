import React, { useState } from 'react';
import { getSalesSummary, getLeaderboard } from '../api/analytics';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useAuth } from '../hooks/useAuth';
import { LeaderboardResponse, SalesSummary } from '../types/analytics';

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
    <div style={{ maxWidth: '900px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>📈 Sales Analytics & Leaderboards</h1>

      <Card title="Lookup Farmer Performance">
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
          <div style={{ flex: 1 }}>
            <Input label="Farmer ID" value={farmerId} onChange={(e) => setFarmerId(e.target.value)} />
          </div>
          <Button onClick={fetchAnalytics} isLoading={loading} style={{ marginBottom: '1rem' }}>
            Fetch Analytics
          </Button>
        </div>
      </Card>

      {summary && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1.5rem', marginTop: '1.5rem' }}>
          <Card title="Total Revenue">
            <p style={{ fontSize: '2rem', fontWeight: 700, color: 'var(--accent-green)' }}>
              ${summary.totalRevenue}
            </p>
          </Card>
          <Card title="Completed Orders">
            <p style={{ fontSize: '2rem', fontWeight: 700, color: 'var(--accent-teal)' }}>
              {summary.totalOrders}
            </p>
          </Card>
        </div>
      )}

      {leaderboard && (
        <Card title="Top Farmers Leaderboard" style={{ marginTop: '2rem' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-muted)' }}>
                <th style={{ padding: '0.75rem' }}>Rank</th>
                <th style={{ padding: '0.75rem' }}>Farmer ID</th>
                <th style={{ padding: '0.75rem' }}>Total Revenue</th>
                <th style={{ padding: '0.75rem' }}>Orders</th>
              </tr>
            </thead>
            <tbody>
              {leaderboard.farmers.map((f) => (
                <tr key={f.farmerId} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                  <td style={{ padding: '0.75rem', fontWeight: 700 }}>#{f.rank}</td>
                  <td style={{ padding: '0.75rem' }}>{f.farmerId}</td>
                  <td style={{ padding: '0.75rem', color: 'var(--accent-green)' }}>${f.totalRevenue}</td>
                  <td style={{ padding: '0.75rem' }}>{f.totalOrders}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
};
