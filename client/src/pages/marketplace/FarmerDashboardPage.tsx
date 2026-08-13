import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getDashboardSummary } from '../../api/dashboardApi';
import { listLowStockProducts } from '../../api/productApi';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { DashboardSummary } from '../../types/dashboard';
import { Product } from '../../types/product';
import { getApiErrorMessage } from '../../utils/apiError';
import '../../styles/marketplace.css';

export const FarmerDashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [lowStock, setLowStock] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([getDashboardSummary(), listLowStockProducts()])
      .then(([s, ls]) => {
        setSummary(s);
        setLowStock(ls);
      })
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load dashboard.')))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner />;
  if (error || !summary) {
    return <div className="marketplace-alert error">{error || 'Unavailable'}</div>;
  }

  const stats = [
    { label: 'Total products', value: summary.totalProducts },
    { label: 'Active', value: summary.activeProducts },
    { label: 'Inactive', value: summary.inactiveProducts },
    { label: 'Low stock', value: summary.lowStockProducts },
    { label: 'Total orders', value: summary.totalOrders },
    { label: 'Pending', value: summary.pendingOrders },
    { label: 'Delivered', value: summary.completedOrders },
    { label: 'Revenue', value: `Rs. ${summary.totalRevenue.toFixed(2)}` },
  ];

  return (
    <div className="marketplace-page">
      <div className="marketplace-header">
        <h1>Farmer Dashboard</h1>
        <p>Overview of your marketplace listings and sales.</p>
      </div>

      <div className="dashboard-stats-grid">
        {stats.map((s) => (
          <Card key={s.label} className="stat-card">
            <p className="stat-label">{s.label}</p>
            <p className="stat-value">{s.value}</p>
          </Card>
        ))}
      </div>

      <div className="dashboard-actions">
        <Link to="/farmer/products"><Card title="Manage products">Create, edit, and deactivate listings →</Card></Link>
        <Link to="/farmer/orders"><Card title="Manage orders">Confirm, ship, and track orders →</Card></Link>
      </div>

      {lowStock.length > 0 && (
        <Card title="Low stock alert" className="low-stock-alert">
          <ul>
            {lowStock.map((p) => (
              <li key={p.id}>{p.cropName} — only {p.quantity} kg left</li>
            ))}
          </ul>
        </Card>
      )}
    </div>
  );
};
