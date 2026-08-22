import React from 'react';
import { SalesSummary } from '../../types/analytics';

interface SalesMetricCardsProps {
  summary: SalesSummary;
}

export const SalesMetricCards: React.FC<SalesMetricCardsProps> = ({ summary }) => {
  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR', maximumFractionDigits: 2 }).format(amount || 0);

  const avgOrderValue = summary.totalOrders > 0 ? summary.totalRevenue / summary.totalOrders : 0;

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1.5rem' }}>
      <div style={{ background: 'rgba(22,163,74,0.14)', border: '1px solid rgba(22,163,74,0.22)', borderRadius: '1.1rem', padding: '1.4rem 1.5rem' }}>
        <span style={{ fontSize: '0.76rem', fontWeight: 600, color: '#64748b', textTransform: 'uppercase' }}>Total Orders</span>
        <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#4ade80' }}>{summary.totalOrders.toLocaleString()}</div>
      </div>
      <div style={{ background: 'rgba(14,165,233,0.14)', border: '1px solid rgba(14,165,233,0.22)', borderRadius: '1.1rem', padding: '1.4rem 1.5rem' }}>
        <span style={{ fontSize: '0.76rem', fontWeight: 600, color: '#64748b', textTransform: 'uppercase' }}>Total Revenue</span>
        <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#38bdf8' }}>{formatCurrency(summary.totalRevenue)}</div>
      </div>
      <div style={{ background: 'rgba(168,85,247,0.14)', border: '1px solid rgba(168,85,247,0.22)', borderRadius: '1.1rem', padding: '1.4rem 1.5rem' }}>
        <span style={{ fontSize: '0.76rem', fontWeight: 600, color: '#64748b', textTransform: 'uppercase' }}>Avg Order Value</span>
        <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#c084fc' }}>{formatCurrency(avgOrderValue)}</div>
      </div>
    </div>
  );
};
