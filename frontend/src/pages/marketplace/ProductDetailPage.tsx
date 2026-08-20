import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getProduct, listProducts } from '../../api/productApi';
import { ProductCard, ProductImage } from '../../components/marketplace';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Spinner } from '../../components/ui/Spinner';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { useToast } from '../../context/ToastContext';
import { Product } from '../../types/product';
import { getApiErrorMessage } from '../../utils/apiError';

export const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { addItem } = useCart();
  const { showToast } = useToast();

  const [product, setProduct] = useState<Product | null>(null);
  const [related, setRelated] = useState<Product[]>([]);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const isBuyer = user?.role === 'BUYER';
  const inStock = product != null && product.quantity > 0;

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getProduct(Number(id))
      .then((data) => {
        setProduct(data);
        setQuantity(1);
        return listProducts({ cropName: data.cropName, size: 4 });
      })
      .then((page) => setRelated(page.content.filter((p) => p.id !== Number(id)).slice(0, 3)))
      .catch((err) => setError(getApiErrorMessage(err, 'Product not found.')))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Spinner />;
  if (error || !product) {
    return (
      <div className="marketplace-page">
        <div className="alert alert-error">{error || 'Not found'}</div>
        <Link to="/marketplace"><Button variant="secondary">Back to marketplace</Button></Link>
      </div>
    );
  }

  const handleAddToCart = () => {
    addItem(product, quantity);
    showToast(`Added ${quantity} kg of ${product.cropName}`, 'success');
  };

  const handleBuyNow = () => {
    addItem(product, quantity);
    navigate('/marketplace/checkout');
  };

  return (
    <div className="marketplace-page product-detail">
      <Link to="/marketplace" className="marketplace-back">← Back to marketplace</Link>

      <div className="product-detail-grid">
        <ProductImage product={product} className="product-detail-image" />
        <div>
          <h1>{product.cropName}</h1>
          <p className="marketplace-meta">{product.location} · Harvest {product.harvestDate}</p>
          <p className="marketplace-price-lg">Rs. {product.unitPrice.toFixed(2)} <span>/ kg</span></p>
          <p className="marketplace-meta">
            {inStock ? `${product.quantity} kg available` : 'Out of stock'} · Status: {product.status}
          </p>
          {product.description && <p className="product-description">{product.description}</p>}

          {isBuyer && inStock && (
            <div className="product-detail-actions">
              <Input
                label="Quantity (kg)"
                type="number"
                min={1}
                max={product.quantity}
                value={quantity}
                onChange={(e) => {
                  const v = Number(e.target.value);
                  if (Number.isNaN(v)) return;
                  setQuantity(Math.max(1, Math.min(v, product.quantity)));
                }}
              />
              <div className="product-detail-buttons">
                <Button onClick={handleAddToCart}>Add to Cart</Button>
                <Button variant="secondary" onClick={handleBuyNow}>Buy Now</Button>
              </div>
            </div>
          )}
        </div>
      </div>

      {related.length > 0 && (
        <section className="related-products">
          <h2>Related produce</h2>
          <div className="marketplace-product-grid">
            {related.map((p) => (
              <ProductCard key={p.id} product={p} isBuyer={isBuyer} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
};
