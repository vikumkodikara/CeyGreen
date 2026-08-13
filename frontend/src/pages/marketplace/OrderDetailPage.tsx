import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getOrder } from '../../api/orderApi';
import { OrderStatusTimeline } from '../../components/marketplace';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { Order } from '../../types/order';
import { getApiErrorMessage } from '../../utils/apiError';
import '../../styles/marketplace.css';

export const OrderDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    getOrder(Number(id))
      .then(setOrder)
      .catch((err) => setError(getApiErrorMessage(err, 'Order not found.')))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Spinner />;
  if (error || !order) {
    return (
      <div className="marketplace-page">
        <div className="marketplace-alert error">{error}</div>
        <Link to="/marketplace/orders"><Button variant="secondary">Back to orders</Button></Link>
      </div>
    );
  }

  return (
    <div className="marketplace-page">
      <Link to="/marketplace/orders" className="marketplace-back">← My orders</Link>
      <h1>Order #{order.id}</h1>

      <Card>
        <OrderStatusTimeline status={order.status} />
        <dl className="order-detail-dl">
          <dt>Product</dt><dd>{order.cropName || order.productId}</dd>
          <dt>Quantity</dt><dd>{order.quantity} kg</dd>
          <dt>Unit price</dt><dd>Rs. {order.unitPrice?.toFixed(2) ?? '—'}</dd>
          <dt>Total</dt><dd>Rs. {order.totalPrice.toFixed(2)}</dd>
          <dt>Ordered</dt><dd>{new Date(order.orderedAt).toLocaleString()}</dd>
          <dt>Ship to</dt>
          <dd>{order.buyerName}, {order.address}, {order.city} {order.postalCode}</dd>
          <dt>Phone</dt><dd>{order.phone}</dd>
        </dl>
      </Card>
    </div>
  );
};
