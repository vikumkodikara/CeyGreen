import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { listCategories, listProducts } from '../../api/productApi';
import { ProductCard } from '../../components/marketplace/ProductCard';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { useDebounce } from '../../hooks/useDebounce';
import { useToast } from '../../context/ToastContext';
import { Product } from '../../types/product';
import { PageHeader } from '../../components/layout/PageHeader';
import { getApiErrorMessage } from '../../utils/apiError';

export const MarketplaceBrowsePage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addItem } = useCart();
  const { showToast } = useToast();

  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [q, setQ] = useState('');
  const [cropName, setCropName] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [inStock, setInStock] = useState(true);
  const [sort, setSort] = useState<'price_asc' | 'price_desc' | 'newest'>('newest');

  const debouncedQ = useDebounce(q);

  const isBuyer = user?.role === 'BUYER';

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const page = await listProducts({
        q: debouncedQ || undefined,
        cropName: cropName || undefined,
        minPrice: minPrice ? Number(minPrice) : undefined,
        maxPrice: maxPrice ? Number(maxPrice) : undefined,
        inStock: inStock || undefined,
        sort,
        page: 0,
        size: 48,
      });
      setProducts(page.content);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to load products.'));
    } finally {
      setLoading(false);
    }
  }, [debouncedQ, cropName, minPrice, maxPrice, inStock, sort]);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="marketplace-page">
      <PageHeader
        title="Marketplace"
        subtitle="Harvests listed by CeyGreen farmers."
      />

      <Card title="Search & Filter">
        <div className="marketplace-filter-grid marketplace-filter-grid-wide">
          <Input label="Search" placeholder="Crop, location…" value={q} onChange={(e) => setQ(e.target.value)} />
          <label className="marketplace-field">
            <span>Category</span>
            <select value={cropName} onChange={(e) => setCropName(e.target.value)} className="marketplace-select">
              <option value="">All crops</option>
              {categories.map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </label>
          <Input label="Min price" type="number" value={minPrice} onChange={(e) => setMinPrice(e.target.value)} />
          <Input label="Max price" type="number" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value)} />
          <label className="marketplace-field marketplace-checkbox">
            <input type="checkbox" checked={inStock} onChange={(e) => setInStock(e.target.checked)} />
            In stock only
          </label>
          <label className="marketplace-field">
            <span>Sort</span>
            <select value={sort} onChange={(e) => setSort(e.target.value as typeof sort)} className="marketplace-select">
              <option value="newest">Newest</option>
              <option value="price_asc">Price: low to high</option>
              <option value="price_desc">Price: high to low</option>
            </select>
          </label>
        </div>
      </Card>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="marketplace-skeleton-grid">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="skeleton-card" />
          ))}
        </div>
      ) : products.length === 0 ? (
        <div className="marketplace-empty glass-panel">
          <p>No listings match your filters.</p>
        </div>
      ) : (
        <div className="marketplace-product-grid">
          {products.map((p) => (
            <ProductCard
              key={p.id}
              product={p}
              isBuyer={isBuyer}
              onAddToCart={(product) => {
                addItem(product, 1);
                showToast(`${product.cropName} added to cart`, 'success');
              }}
              onBuyNow={() => navigate(`/marketplace/products/${p.id}`)}
            />
          ))}
        </div>
      )}
    </div>
  );
};
