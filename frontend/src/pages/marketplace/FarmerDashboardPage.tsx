import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getDashboardSummary } from '../../api/dashboardApi';
import { listLowStockProducts } from '../../api/productApi';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { PageHeader } from '../../components/layout/PageHeader';
import { DashboardSummary } from '../../types/dashboard';
import { Product } from '../../types/product';
import { getApiErrorMessage } from '../../utils/apiError';

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
    return (
      <div className="marketplace-page">
        <div className="alert alert-error">{error || 'Unavailable'}</div>
      </div>
    );
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
      <PageHeader
        title="Farm sales"
        subtitle="Listings, stock, and order status."
      />

      <div className="dashboard-stats-grid">
        {stats.map((s) => (
          <div key={s.label} className="stat-tile">
            <div className="k">{s.label}</div>
            <div className="v">{s.value}</div>
          </div>
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
