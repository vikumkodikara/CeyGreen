import React, { useEffect, useState } from 'react';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Product } from '../../types/product';

interface CheckoutModalProps {
  isOpen: boolean;
  product: Product | null;
  loading?: boolean;
  error?: string | null;
  onClose: () => void;
  onConfirm: (quantity: number) => void;
}

export const CheckoutModal: React.FC<CheckoutModalProps> = ({
  isOpen,
  product,
  loading,
  error,
  onClose,
  onConfirm,
}) => {
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    if (product) setQuantity(1);
  }, [product]);

  if (!product) return null;

  const total = Number((product.unitPrice * quantity).toFixed(2));
  const overStock = quantity > product.quantity;
  const invalidQty = quantity < 1;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Checkout — ${product.cropName}`}>
      <p className="marketplace-meta">{product.location} · Rs. {product.unitPrice} / kg</p>
      <p className="marketplace-meta" style={{ marginBottom: '1rem' }}>
        Available: {product.quantity} kg
      </p>

      {error && <div className="marketplace-alert error">{error}</div>}

      <Input
        label="Quantity (kg)"
        type="number"
        min={1}
        max={product.quantity}
        value={quantity}
        onChange={(e) => {
          const next = e.currentTarget.valueAsNumber;
          setQuantity(Number.isNaN(next) ? quantity : next);
        }}
      />

      {(overStock || invalidQty) && (
        <div className="marketplace-alert error" style={{ marginTop: '0.75rem' }}>
          {overStock ? 'Quantity exceeds available stock.' : 'Enter a valid quantity.'}
        </div>
      )}

      <div className="marketplace-checkout-total">
        <span>Total</span>
        <span style={{ color: 'var(--accent-green)' }}>Rs. {total}</span>
      </div>

      <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
        <Button variant="secondary" onClick={onClose} disabled={loading}>
          Cancel
        </Button>
        <Button
          onClick={() => onConfirm(quantity)}
          isLoading={loading}
          disabled={overStock || invalidQty || product.quantity === 0}
        >
          Confirm Purchase
        </Button>
      </div>
    </Modal>
  );
};
