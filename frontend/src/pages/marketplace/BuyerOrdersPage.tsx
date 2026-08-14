import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyOrders } from '../../api/orderApi';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { OrderStatus } from '../../types/order';
import { getApiErrorMessage } from '../../utils/apiError';
import '../../styles/marketplace.css';

export const BuyerOrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Awaited<ReturnType<typeof getMyOrders>>['content']>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<OrderStatus | ''>('');

  useEffect(() => {
    setLoading(true);
    getMyOrders({ status: statusFilter || undefined, page: 0, size: 50 })
      .then((page) => setOrders(page.content))
      .catch((err) => setError(getApiErrorMessage(err, 'Failed to load orders.')))
      .finally(() => setLoading(false));
  }, [statusFilter]);

  return (
    <div className="marketplace-page">
      <h1>My Orders</h1>
      <label className="marketplace-field">
        <span>Filter by status</span>
        <select
          className="marketplace-select"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as OrderStatus | '')}
        >
          <option value="">All</option>
          {(['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'] as OrderStatus[]).map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </label>

      {error && <div className="marketplace-alert error">{error}</div>}
      {loading ? (
        <Spinner />
      ) : orders.length === 0 ? (
        <div className="marketplace-empty glass-panel">No orders yet.</div>
      ) : (
        <div className="orders-table-wrap">
          <table className="marketplace-table">
            <thead>
              <tr>
                <th>Order</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Total</th>
                <th>Status</th>
                <th>Date</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {orders.map((o) => (
                <tr key={o.id}>
                  <td>#{o.id}</td>
                  <td>{o.cropName || `Product ${o.productId}`}</td>
                  <td>{o.quantity}</td>
                  <td>Rs. {o.totalPrice.toFixed(2)}</td>
                  <td><span className={`status-badge status-${o.status.toLowerCase()}`}>{o.status}</span></td>
                  <td>{new Date(o.orderedAt).toLocaleString()}</td>
                  <td><Link to={`/marketplace/orders/${o.id}`}>View</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
