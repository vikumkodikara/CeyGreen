import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Product } from '../../types/product';

interface ProductCardProps {
  product: Product;
  isBuyer?: boolean;
  isOwner?: boolean;
  actionLoading?: boolean;
  onBuy?: (product: Product) => void;
  children?: React.ReactNode;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  isBuyer,
  isOwner,
  actionLoading,
  onBuy,
  children,
}) => {
  return (
    <Card title={product.cropName} subtitle={`${product.location} · Harvest ${product.harvestDate}`}>
      <p className="marketplace-meta">Farmer: {product.farmerId.slice(0, 8)}…</p>
      <p className="marketplace-price">
        Rs. {product.unitPrice}{' '}
        <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 400 }}>/ kg</span>
      </p>
      <p className="marketplace-meta" style={{ marginBottom: '1rem' }}>
        {product.quantity > 0 ? `${product.quantity} kg in stock` : 'Out of stock'}
      </p>

      {children}

      <div style={{ display: 'flex', gap: '0.5rem', marginTop: children ? '0.75rem' : 0 }}>
        <Link to={`/marketplace/products/${product.id}`} style={{ flex: 1 }}>
          <Button variant="secondary" size="sm" style={{ width: '100%' }}>
            View Details
          </Button>
        </Link>
        {isBuyer && product.quantity > 0 && onBuy && (
          <Button size="sm" style={{ flex: 1 }} isLoading={actionLoading} onClick={() => onBuy(product)}>
            Buy
          </Button>
        )}
        {isOwner && (
          <Link to={`/marketplace/products/${product.id}`} style={{ flex: 1 }}>
            <Button variant="ghost" size="sm" style={{ width: '100%' }}>
              Manage
            </Button>
          </Link>
        )}
      </div>
    </Card>
  );
};
