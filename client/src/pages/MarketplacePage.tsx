import React, { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom';
import { listProducts, createProduct, updateProduct, checkout } from '../api/products';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card } from '../components/ui/Card';
import { Spinner } from '../components/ui/Spinner';
import { ProductCard } from '../components/marketplace/ProductCard';
import { CheckoutModal } from '../components/marketplace/CheckoutModal';
import { FarmerListingForm } from '../components/marketplace/FarmerListingForm';
import { FarmerListingEditor } from '../components/marketplace/FarmerListingEditor';
import { Product, ProductCreateRequest } from '../types/product';
import { useAuth } from '../hooks/useAuth';
import { getApiErrorMessage } from '../utils/apiError';
import '../styles/marketplace.css';

const todayIso = () => { const d = new Date(); d.setMinutes(d.getMinutes() - d.getTimezoneOffset()); return d.toISOString().slice(0, 10); };

const emptyCreateForm = (): ProductCreateRequest => ({
  cropName: '',
  quantity: 20,
  unitPrice: 150,
  harvestDate: todayIso(),
  location: '',
});

type Tab = 'browse' | 'farmer';

export const MarketplacePage: React.FC = () => {
  const { user } = useAuth();
  const location = useLocation();

  const [tab, setTab] = useState<Tab>(
    (location.state as { tab?: Tab })?.tab === 'farmer' ? 'farmer' : 'browse'
  );
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState<number | null>(null);
  const [pageError, setPageError] = useState<string | null>(null);
  const [pageSuccess, setPageSuccess] = useState<string | null>(
    (location.state as { message?: string })?.message || null
  );

  const [cropFilter, setCropFilter] = useState('');
  const [locationFilter, setLocationFilter] = useState('');
  const [createForm, setCreateForm] = useState<ProductCreateRequest>(emptyCreateForm());
  const [editDrafts, setEditDrafts] = useState<Record<number, { quantity: number; unitPrice: number }>>({});

  const [checkoutProduct, setCheckoutProduct] = useState<Product | null>(null);
  const [checkoutError, setCheckoutError] = useState<string | null>(null);

  const isFarmer = user?.role === 'FARMER';
  const isBuyer = user?.role === 'BUYER';
  const farmerId = user?.farmerId || user?.id;

  const fetchProducts = useCallback(async (crop?: string, loc?: string) => {
    setLoading(true);
    setPageError(null);
    try {
      const data = await listProducts(crop, loc);
      setProducts(data);
      const editMap: Record<number, { quantity: number; unitPrice: number }> = {};
      data.forEach((p) => {
        editMap[p.id] = { quantity: p.quantity, unitPrice: p.unitPrice };
      });
      setEditDrafts(editMap);
    } catch (err) {
      setPageError(getApiErrorMessage(err, 'Failed to load products.'));
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handleFilter = async (e: React.FormEvent) => {
    e.preventDefault();
    await fetchProducts(cropFilter, locationFilter);
  };

  const handleClearFilters = async () => {
    setCropFilter('');
    setLocationFilter('');
    await fetchProducts();
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFarmer) return;
    setActionLoading(-1);
    setPageError(null);
    setPageSuccess(null);
    try {
      await createProduct(createForm);
      setCreateForm(emptyCreateForm());
      setPageSuccess('Harvest listed successfully.');
      await fetchProducts(cropFilter, locationFilter);
      setTab('farmer');
    } catch (err) {
      setPageError(getApiErrorMessage(err, 'Failed to list product.'));
    } finally {
      setActionLoading(null);
    }
  };

  const handleUpdate = async (product: Product) => {
    const draft = editDrafts[product.id];
    if (!draft) return;
    setActionLoading(product.id);
    setPageError(null);
    try {
      await updateProduct(product.id, {
        quantity: draft.quantity,
        unitPrice: draft.unitPrice,
      });
      setPageSuccess(`${product.cropName} updated.`);
      await fetchProducts(cropFilter, locationFilter);
    } catch (err) {
      setPageError(getApiErrorMessage(err, 'Failed to update listing.'));
    } finally {
      setActionLoading(null);
    }
  };

  const handleDeactivate = async (product: Product) => {
    if (!window.confirm(`Deactivate listing for ${product.cropName}?`)) return;
    setActionLoading(product.id);
    setPageError(null);
    try {
      await updateProduct(product.id, { active: false });
      setPageSuccess(`${product.cropName} marked inactive.`);
      await fetchProducts(cropFilter, locationFilter);
    } catch (err) {
      setPageError(getApiErrorMessage(err, 'Failed to deactivate listing.'));
    } finally {
      setActionLoading(null);
    }
  };

  const handleCheckout = async (quantity: number) => {
    if (!checkoutProduct) return;
    setActionLoading(checkoutProduct.id);
    setCheckoutError(null);
    try {
      const order = await checkout({ productId: checkoutProduct.id, quantity });
      setCheckoutProduct(null);
      setPageSuccess(`Order #${order.id} placed — Total Rs. ${order.totalPrice}`);
      await fetchProducts(cropFilter, locationFilter);
    } catch (err) {
      setCheckoutError(getApiErrorMessage(err, 'Checkout failed.'));
    } finally {
      setActionLoading(null);
    }
  };

  const ownsListing = (product: Product) =>
    isFarmer && farmerId != null && product.farmerId === farmerId;

  const myListings = products.filter(ownsListing);
  const browseProducts = tab === 'farmer' && isFarmer ? myListings : products;

  return (
    <div className="marketplace-page">
      <div className="marketplace-header">
        <h1>🛒 Crop Marketplace</h1>
        <p>Browse fresh harvests, list your produce, and purchase directly from farmers.</p>
      </div>

      {pageError && <div className="marketplace-alert error">{pageError}</div>}
      {pageSuccess && <div className="marketplace-alert success">{pageSuccess}</div>}

      <div className="marketplace-tabs">
        <button
          type="button"
          className={`marketplace-tab ${tab === 'browse' ? 'active' : ''}`}
          onClick={() => setTab('browse')}
        >
          Browse Produce
        </button>
        {isFarmer && (
          <button
            type="button"
            className={`marketplace-tab ${tab === 'farmer' ? 'active' : ''}`}
            onClick={() => setTab('farmer')}
          >
            My Listings
          </button>
        )}
      </div>

      {tab === 'browse' && (
        <Card title="Search & Filter">
          <form className="marketplace-filter-grid" onSubmit={handleFilter}>
            <Input
              label="Crop type"
              placeholder="e.g. Tomato"
              value={cropFilter}
              onChange={(e) => setCropFilter(e.target.value)}
            />
            <Input
              label="Location"
              placeholder="e.g. Kandy"
              value={locationFilter}
              onChange={(e) => setLocationFilter(e.target.value)}
            />
            <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
              Search
            </Button>
            <Button type="button" variant="secondary" onClick={handleClearFilters} style={{ marginBottom: '1rem' }}>
              Clear
            </Button>
          </form>
        </Card>
      )}

      {tab === 'farmer' && isFarmer && (
        <div style={{ marginTop: '1.5rem' }}>
          <FarmerListingForm
            value={createForm}
            loading={actionLoading === -1}
            onChange={setCreateForm}
            onSubmit={handleCreate}
          />
        </div>
      )}

      <h2 style={{ fontSize: '1.35rem', marginTop: '2rem', marginBottom: '1rem' }}>
        {tab === 'farmer' && isFarmer ? 'Your Active Listings' : 'Available Produce'}
      </h2>

      {loading ? (
        <Spinner />
      ) : browseProducts.length === 0 ? (
        <div className="marketplace-empty glass-panel">
          <p style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>🌱</p>
          <p>
            {tab === 'farmer'
              ? 'You have no active listings yet. Create one above.'
              : 'No active listings found. Try adjusting your filters.'}
          </p>
        </div>
      ) : tab === 'farmer' && isFarmer ? (
        <div className="marketplace-product-grid">
          {browseProducts.map((p) => (
            <FarmerListingEditor
              key={p.id}
              product={p}
              quantity={editDrafts[p.id]?.quantity ?? p.quantity}
              unitPrice={editDrafts[p.id]?.unitPrice ?? p.unitPrice}
              loading={actionLoading === p.id}
              onQuantityChange={(qty) =>
                setEditDrafts((prev) => ({
                  ...prev,
                  [p.id]: { quantity: qty, unitPrice: prev[p.id]?.unitPrice ?? p.unitPrice },
                }))
              }
              onPriceChange={(price) =>
                setEditDrafts((prev) => ({
                  ...prev,
                  [p.id]: { quantity: prev[p.id]?.quantity ?? p.quantity, unitPrice: price },
                }))
              }
              onSave={() => handleUpdate(p)}
              onDeactivate={() => handleDeactivate(p)}
            />
          ))}
        </div>
      ) : (
        <div className="marketplace-product-grid">
          {browseProducts.map((p) => (
            <ProductCard
              key={p.id}
              product={p}
              isBuyer={isBuyer}
              isOwner={ownsListing(p)}
              actionLoading={actionLoading === p.id}
              onBuy={(product) => {
                setCheckoutError(null);
                setCheckoutProduct(product);
              }}
            />
          ))}
        </div>
      )}

      <CheckoutModal
        isOpen={checkoutProduct != null}
        product={checkoutProduct}
        loading={actionLoading != null && checkoutProduct != null && actionLoading === checkoutProduct.id}
        error={checkoutError}
        onClose={() => {
          setCheckoutProduct(null);
          setCheckoutError(null);
        }}
        onConfirm={handleCheckout}
      />
    </div>
  );
};
