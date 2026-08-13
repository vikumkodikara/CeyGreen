import React, { useEffect, useState } from 'react';
import { getFarmerOrders, updateOrderStatus } from '../../api/orderApi';
import { ConfirmDialog } from '../../components/marketplace';
import { Button } from '../../components/ui/Button';
import { Spinner } from '../../components/ui/Spinner';
import { Order, OrderStatus } from '../../types/order';
import { useToast } from '../../context/ToastContext';
import { getApiErrorMessage } from '../../utils/apiError';
import '../../styles/marketplace.css';

const NEXT_STATUS: Partial<Record<OrderStatus, OrderStatus>> = {
  PENDING: 'CONFIRMED',
  CONFIRMED: 'SHIPPED',
  SHIPPED: 'DELIVERED',
};

export const FarmerOrdersPage: React.FC = () => {
  const { showToast } = useToast();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [pendingUpdate, setPendingUpdate] = useState<{ order: Order; status: OrderStatus } | null>(null);
  const [actionId, setActionId] = useState<number | null>(null);

  const load = () => {
    setLoading(true);
    getFarmerOrders({ page: 0, size: 100 })
      .then((page) => setOrders(page.content))
      .catch((err) => showToast(getApiErrorMessage(err, 'Failed to load orders.'), 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const applyStatus = async () => {
    if (!pendingUpdate) return;
    setActionId(pendingUpdate.order.id);
    try {
      await updateOrderStatus(pendingUpdate.order.id, pendingUpdate.status);
      showToast(`Order #${pendingUpdate.order.id} → ${pendingUpdate.status}`, 'success');
      setPendingUpdate(null);
      load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Status update failed.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="marketplace-page">
      <h1>Farmer Orders</h1>
      <div className="orders-table-wrap">
        <table className="marketplace-table">
          <thead>
            <tr>
              <th>Order</th>
              <th>Product</th>
              <th>Qty</th>
              <th>Total</th>
              <th>Buyer</th>
              <th>Status</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => {
              const next = NEXT_STATUS[o.status];
              return (
                <tr key={o.id}>
                  <td>#{o.id}</td>
                  <td>{o.cropName}</td>
                  <td>{o.quantity}</td>
                  <td>Rs. {o.totalPrice.toFixed(2)}</td>
                  <td>{o.buyerName || o.buyerId.slice(0, 8)}</td>
                  <td><span className={`status-badge status-${o.status.toLowerCase()}`}>{o.status}</span></td>
                  <td>{new Date(o.orderedAt).toLocaleDateString()}</td>
                  <td>
                    {next && (
                      <Button
                        size="sm"
                        onClick={() => setPendingUpdate({ order: o, status: next })}
                        disabled={actionId === o.id}
                      >
                        Mark {next.toLowerCase()}
                      </Button>
                    )}
                    {o.status === 'PENDING' && (
                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => setPendingUpdate({ order: o, status: 'CANCELLED' })}
                      >
                        Cancel
                      </Button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <ConfirmDialog
        open={pendingUpdate != null}
        title="Update order status"
        message={
          pendingUpdate
            ? `Change order #${pendingUpdate.order.id} to ${pendingUpdate.status}?`
            : ''
        }
        loading={pendingUpdate != null && actionId === pendingUpdate.order.id}
        onConfirm={applyStatus}
        onCancel={() => setPendingUpdate(null)}
      />
    </div>
  );
};
