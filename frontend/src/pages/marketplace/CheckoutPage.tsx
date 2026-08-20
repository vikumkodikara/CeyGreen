import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { checkout } from '../../api/orderApi';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { PageHeader } from '../../components/layout/PageHeader';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { useToast } from '../../context/ToastContext';
import { getApiErrorMessage } from '../../utils/apiError';

export const CheckoutPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { items, subtotal, emptyCart } = useCart();
  const { showToast } = useToast();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [buyerName, setBuyerName] = useState(user?.name || '');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [city, setCity] = useState('');
  const [postalCode, setPostalCode] = useState('');

  if (user?.role !== 'BUYER') {
    return (
      <div className="marketplace-page">
        <div className="alert alert-error">Only buyers can checkout.</div>
      </div>
    );
  }

  if (items.length === 0) {
    return (
      <div className="marketplace-page">
        <PageHeader title="Checkout" />
        <div className="marketplace-empty glass-panel">
          <p>Your cart is empty.</p>
          <Link to="/marketplace"><Button>Browse products</Button></Link>
        </div>
      </div>
    );
  }

  const validate = () => {
    if (!buyerName.trim() || !phone.trim() || !address.trim() || !city.trim() || !postalCode.trim()) {
      setError('Please complete all shipping fields.');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    setError(null);
    try {
      const result = await checkout({
        items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
        buyerName: buyerName.trim(),
        phone: phone.trim(),
        address: address.trim(),
        city: city.trim(),
        postalCode: postalCode.trim(),
      });
      emptyCart();
      showToast(`Order placed — ${result.orders.length} item(s)`, 'success');
      navigate('/marketplace/orders');
    } catch (err) {
      setError(getApiErrorMessage(err, 'Checkout failed.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="marketplace-page">
      <PageHeader title="Checkout" subtitle="Enter shipping details to complete your order." />
      {error && <div className="alert alert-error">{error}</div>}

      <div className="checkout-layout">
        <Card title="Shipping information">
          <form className="checkout-form" onSubmit={handleSubmit}>
            <Input label="Full name" value={buyerName} onChange={(e) => setBuyerName(e.target.value)} required />
            <Input label="Phone" value={phone} onChange={(e) => setPhone(e.target.value)} required />
            <Input label="Address" value={address} onChange={(e) => setAddress(e.target.value)} required />
            <Input label="City" value={city} onChange={(e) => setCity(e.target.value)} required />
            <Input label="Postal code" value={postalCode} onChange={(e) => setPostalCode(e.target.value)} required />
            <Button type="submit" isLoading={loading} style={{ marginTop: '1rem' }}>
              Place order — Rs. {subtotal.toFixed(2)}
            </Button>
          </form>
        </Card>

        <Card title="Order summary">
          <ul className="checkout-summary-list">
            {items.map((i) => (
              <li key={i.productId}>
                {i.cropName} × {i.quantity} kg — Rs. {(i.unitPrice * i.quantity).toFixed(2)}
              </li>
            ))}
          </ul>
          <p className="checkout-total">Total: <strong>Rs. {subtotal.toFixed(2)}</strong></p>
        </Card>
      </div>
    </div>
  );
};
