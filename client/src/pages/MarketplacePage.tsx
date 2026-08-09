import React, { useEffect, useState, useCallback } from 'react';
import { listProducts }
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input'; from '../api/products';
import { Spinner } from '../components/ui/Spinner';
import { Product } from '../types/product';
import { getApiErrorMessage } from '../utils/apiError';
import '../styles/marketplace.css';

export const MarketplacePage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [cropFilter, setCropFilter] = useState('');
  const [locationFilter, setLocationFilter] = useState('');
  const [pageError, setPageError] = useState<string | null>(null);

  const fetchProducts = useCallback(async (crop?: string, loc?: string) => {
    setLoading(true);
    setPageError(null);
    try {
      setProducts(await listProducts(crop, loc));
    } catch (err) {
      setPageError(getApiErrorMessage(err, 'Failed to load products.'));
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchProducts(); }, [fetchProducts]);

  return (
    <div className="marketplace-page">
      <div className="marketplace-header">
        <h1>?? Crop Marketplace</h1>
        <p>Browse fresh harvests from local farmers.</p>
      </div>
      <Card title="Search & Filter" style={{ marginBottom: '1.5rem' }}>
        <form className="marketplace-filter-grid" onSubmit={(e) => { e.preventDefault(); fetchProducts(cropFilter, locationFilter); }}>
          <Input label="Crop type" value={cropFilter} onChange={(e) => setCropFilter(e.target.value)} />
          <Input label="Location" value={locationFilter} onChange={(e) => setLocationFilter(e.target.value)} />
          <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>Search</Button>
          <Button type="button" variant="secondary" style={{ marginBottom: '1rem' }} onClick={() => { setCropFilter(''); setLocationFilter(''); fetchProducts(); }}>Clear</Button>
        </form>
      </Card>
      {pageError && <div className="marketplace-alert error">{pageError}</div>}
      {loading ? <Spinner /> : products.length === 0 ? (
        <div className="marketplace-empty glass-panel"><p>No active listings found.</p></div>
      ) : (
        <div className="marketplace-product-grid">
          {products.map((p) => (
            <div key={p.id} className="glass-panel" style={{ padding: '1rem' }}>
              <h3>{p.cropName}</h3>
              <p className="marketplace-meta">{p.location} · {p.harvestDate}</p>
              <p className="marketplace-price">Rs. {p.unitPrice} / kg</p>
              <p className="marketplace-meta">{p.quantity} kg available</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};