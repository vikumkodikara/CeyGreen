import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { ProductImage } from '../../components/marketplace/ProductImage';
import '../../styles/marketplace.css';

export const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { items, removeItem, updateQuantity, subtotal, emptyCart } = useCart();

  if (user?.role !== 'BUYER') {
    return (
      <div className="marketplace-page">
        <div className="marketplace-alert error">Only buyers can access the cart.</div>
      </div>
    );
  }

  if (items.length === 0) {
    return (
      <div className="marketplace-page">
        <h1>Your Cart</h1>
        <div className="marketplace-empty glass-panel">
          <p>Your cart is empty.</p>
          <Link to="/marketplace"><Button>Browse marketplace</Button></Link>
        </div>
      </div>
    );
  }

  return (
    <div className="marketplace-page">
      <div className="marketplace-header">
        <h1>Your Cart</h1>
        <Button variant="ghost" onClick={emptyCart}>Clear cart</Button>
      </div>

      <div className="cart-layout">
        <div className="cart-items">
          {items.map((item) => (
            <Card key={item.productId} className="cart-item">
              <ProductImage
                product={{
                  id: item.productId,
                  cropName: item.cropName,
                  imageUrl: item.imageUrl,
                } as never}
              />
              <div className="cart-item-info">
                <h3>{item.cropName}</h3>
                <p className="marketplace-meta">Rs. {item.unitPrice.toFixed(2)} / kg</p>
                <div className="cart-qty-row">
                  <Button size="sm" variant="secondary" onClick={() => updateQuantity(item.productId, item.quantity - 1)}>-</Button>
                  <span>{item.quantity} kg</span>
                  <Button size="sm" variant="secondary" onClick={() => updateQuantity(item.productId, item.quantity + 1)}>+</Button>
                </div>
              </div>
              <div className="cart-item-total">
                <strong>Rs. {(item.unitPrice * item.quantity).toFixed(2)}</strong>
                <Button size="sm" variant="danger" onClick={() => removeItem(item.productId)}>Remove</Button>
              </div>
            </Card>
          ))}
        </div>

        <Card title="Order summary" className="cart-summary">
          <p>Subtotal: <strong>Rs. {subtotal.toFixed(2)}</strong></p>
          <Button style={{ width: '100%', marginTop: '1rem' }} onClick={() => navigate('/marketplace/checkout')}>
            Proceed to checkout
          </Button>
        </Card>
      </div>
    </div>
  );
};
