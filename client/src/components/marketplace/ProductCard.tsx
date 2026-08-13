import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Product } from '../../types/product';
import { ProductImage } from './ProductImage';

interface ProductCardProps {
  product: Product;
  isBuyer?: boolean;
  actionLoading?: boolean;
  onAddToCart?: (product: Product) => void;
  onBuyNow?: (product: Product) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  isBuyer,
  actionLoading,
  onAddToCart,
  onBuyNow,
}) => {
  const inStock = product.quantity > 0 && product.status !== 'OUT_OF_STOCK';

  return (
    <Card className="product-card">
      <ProductImage product={product} />
      <div className="product-card-body">
        <h3 className="product-card-title">{product.cropName}</h3>
        <p className="marketplace-meta">{product.location}</p>
        <p className="marketplace-price">
          Rs. {product.unitPrice.toFixed(2)}
          <span> / kg</span>
        </p>
        <p className="marketplace-meta">
          {inStock ? `${product.quantity} kg available` : 'Out of stock'}
        </p>
        <div className="product-card-actions">
          <Link to={`/marketplace/products/${product.id}`}>
            <Button variant="secondary" size="sm">
              View Details
            </Button>
          </Link>
          {isBuyer && inStock && (
            <>
              {onAddToCart && (
                <Button size="sm" variant="ghost" onClick={() => onAddToCart(product)}>
                  Add to Cart
                </Button>
              )}
              {onBuyNow && (
                <Button size="sm" isLoading={actionLoading} onClick={() => onBuyNow(product)}>
                  Buy Now
                </Button>
              )}
            </>
          )}
        </div>
      </div>
    </Card>
  );
};
