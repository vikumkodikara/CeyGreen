import React, { useEffect, useState, useCallback } from 'react';
import { listProducts } from '../api/products';
import { Spinner } from '../components/ui/Spinner';
import { Product } from '../types/product';
import { getApiErrorMessage } from '../utils/apiError';
import '../styles/marketplace.css';

export const MarketplacePage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [pageError, setPageError] = useState<string | null>(null);

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setPageError(null);
    try {
      setProducts(await listProducts());
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