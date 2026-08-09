import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getProduct, updateProduct, checkout } from '../api/products';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Spinner } from '../components/ui/Spinner';
import { CheckoutModal } from '../components/marketplace/CheckoutModal';
import { useAuth } from '../hooks/useAuth';
import { Product } from '../types/product';
import { getApiErrorMessage } from '../utils/apiError';
import '../styles/marketplace.css';

export const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [checkoutOpen, setCheckoutOpen] = useState(false);
  const [checkoutError, setCheckoutError] = useState<string | null>(null);

  const [editQty, setEditQty] = useState(0);
  const [editPrice, setEditPrice] = useState(0);

  const isFarmer = user?.role === 'FARMER';
  const isBuyer = user?.role === 'BUYER';
  const farmerId = user?.farmerId || user?.id;
  const isOwner = isFarmer && product != null && product.farmerId === farmerId;

  const loadProduct = async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const data = await getProduct(Number(id));
      setProduct(data);
      setEditQty(data.quantity);
      setEditPrice(data.unitPrice);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Product not found.'));
      setProduct(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProduct();
  }, [id]);

  const handleSave = async () => {
    if (!product) return;
    setActionLoading(true);
    setSuccess(null);
    setError(null);
    try {
      const updated = await updateProduct(product.id, {
        quantity: editQty,
        unitPrice: editPrice,
      });
      setProduct(updated);
      setSuccess('Listing updated successfully.');
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to update listing.'));
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeactivate = async () => {
    if (!product || !window.confirm(`Mark ${product.cropName} as inactive?`)) return;
    setActionLoading(true);
    setError(null);
    try {
      await updateProduct(product.id, { active: false });
      navigate('/marketplace', { state: { tab: 'farmer', message: 'Listing marked inactive.' } });
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to deactivate listing.'));
      setActionLoading(false);
    }
  };

  const handleCheckout = async (quantity: number) => {
    if (!product) return;
    setActionLoading(true);
    setCheckoutError(null);
    try {
      const order = await checkout({ productId: product.id, quantity });
      setCheckoutOpen(false);
      setSuccess(`Order #${order.id} placed — Total Rs. ${order.totalPrice}`);
      await loadProduct();
    } catch (err) {
      setCheckoutError(getApiErrorMessage(err, 'Checkout failed.'));
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <Spinner />;

  if (!product) {
    return (
      <div className="marketplace-page">
        <div className="marketplace-alert error">{error || 'Product not found.'}</div>
        <Link to="/marketplace">
          <Button variant="secondary">Back to Marketplace</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="marketplace-page">
      <Link to="/marketplace" style={{ fontSize: '0.9rem', display: 'inline-block', marginBottom: '1rem' }}>
        ← Back to Marketplace
      </Link>

      <div className="marketplace-header">
        <h1>{product.cropName}</h1>
        <p>{product.location} · Harvested {product.harvestDate}</p>
      </div>

      {error && <div className="marketplace-alert error">{error}</div>}
      {success && <div className="marketplace-alert success">{success}</div>}

      <div className="marketplace-detail-grid">
        <Card title="Product Information">
          <div className="marketplace-detail-row">
            <span>Crop</span>
            <span>{product.cropName}</span>
          </div>
          <div className="marketplace-detail-row">
            <span>Location</span>
            <span>{product.location}</span>
          </div>
          <div className="marketplace-detail-row">
            <span>Harvest date</span>
            <span>{product.harvestDate}</span>
          </div>
          <div className="marketplace-detail-row">
            <span>Unit price</span>
            <span>Rs. {product.unitPrice} / kg</span>
          </div>
          <div className="marketplace-detail-row">
            <span>Available stock</span>
            <span>{product.quantity} kg</span>
          </div>
          <div className="marketplace-detail-row">
            <span>Farmer ID</span>
            <span style={{ fontSize: '0.85rem', wordBreak: 'break-all' }}>{product.farmerId}</span>
          </div>
          <div className="marketplace-detail-row" style={{ borderBottom: 'none' }}>
            <span>Status</span>
            <span style={{ color: product.active ? 'var(--accent-green)' : 'var(--danger)' }}>
              {product.active ? 'Active' : 'Inactive'}
            </span>
          </div>
        </Card>

        {isOwner && product.active && (
          <Card title="Manage Listing">
            <Input
              label="Quantity (kg)"
              type="number"
              min="0"
              step="1"
              value={editQty}
              onChange={(e) =>
                setEditQty((prev) => {
                  const next = e.currentTarget.valueAsNumber;
                  return Number.isNaN(next) ? prev : Math.max(0, Math.floor(next));
                })
              }
            />
            <Input
              label="Unit price (Rs.)"
              type="number"
              step="0.01"
              min="0.01"
              value={editPrice}
              onChange={(e) =>
                setEditPrice((prev) =>
                  Number.isNaN(e.currentTarget.valueAsNumber) ? prev : e.currentTarget.valueAsNumber
                )
              }
            />
            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', flexWrap: 'wrap' }}>
              <Button isLoading={actionLoading} onClick={handleSave}>
                Save Changes
              </Button>
              <Button variant="danger" disabled={actionLoading} onClick={handleDeactivate}>
                Mark Inactive
              </Button>
            </div>
          </Card>
        )}

        {isBuyer && product.active && product.quantity > 0 && (
          <Card title="Purchase">
            <p className="marketplace-meta" style={{ marginBottom: '1rem' }}>
              Select quantity and confirm your order.
            </p>
            <Button onClick={() => { setCheckoutError(null); setCheckoutOpen(true); }}>
              Proceed to Checkout
            </Button>
          </Card>
        )}

        {isBuyer && product.quantity === 0 && (
          <Card title="Purchase">
            <div className="marketplace-alert error">This product is currently out of stock.</div>
          </Card>
        )}
      </div>

      <CheckoutModal
        isOpen={checkoutOpen}
        product={product}
        loading={actionLoading}
        error={checkoutError}
        onClose={() => setCheckoutOpen(false)}
        onConfirm={handleCheckout}
      />
    </div>
  );
};
