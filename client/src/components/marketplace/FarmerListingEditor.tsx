import React from 'react';
import { Card } from '../ui/Card';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Product } from '../../types/product';

interface FarmerListingEditorProps {
  product: Product;
  quantity: number;
  unitPrice: number;
  loading?: boolean;
  onQuantityChange: (qty: number) => void;
  onPriceChange: (price: number) => void;
  onSave: () => void;
  onDeactivate: () => void;
}

export const FarmerListingEditor: React.FC<FarmerListingEditorProps> = ({
  product,
  quantity,
  unitPrice,
  loading,
  onQuantityChange,
  onPriceChange,
  onSave,
  onDeactivate,
}) => (
  <Card title={product.cropName} subtitle={`${product.location} · Listed ${product.harvestDate}`}>
    <p className="marketplace-meta">Stock: {product.quantity} kg · Rs. {product.unitPrice} / kg</p>
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '1rem' }}>
      <Input
        label="Update quantity (kg)"
        type="number"
        min="0"
        value={quantity}
        onChange={(e) => onQuantityChange(parseInt(e.target.value, 10))}
      />
      <Input
        label="Update price (Rs.)"
        type="number"
        step="0.01"
        min="0.01"
        value={unitPrice}
        onChange={(e) => onPriceChange(parseFloat(e.target.value))}
      />
      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
        <Button size="sm" variant="secondary" isLoading={loading} onClick={onSave}>
          Save Changes
        </Button>
        <Button size="sm" variant="danger" disabled={loading} onClick={onDeactivate}>
          Mark Inactive
        </Button>
      </div>
    </div>
  </Card>
);
