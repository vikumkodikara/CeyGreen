import React, { useState, useEffect } from 'react';
import { listProducts, createProduct, checkout } from '../api/products';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Product } from '../types/product';
import { useAuth } from '../hooks/useAuth';

export const MarketplacePage: React.FC = () => {
  const { user } = useAuth();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);

  // New product state
  const [name, setName] = useState('');
  const [price, setPrice] = useState(10);
  const [quantity, setQuantity] = useState(100);
  const [cropType, setCropType] = useState('Organic');

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const data = await listProducts();
      setProducts(data);
    } catch {
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-1';
      await createProduct({ name, price, quantity, cropType, farmerId });
      setName('');
      fetchProducts();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to list product');
    }
  };

  const handleBuy = async (product: Product) => {
    try {
      const buyerId = user?.buyerId || user?.id || 'buyer-1';
      const order = await checkout({
        buyerId,
        items: [{ productId: product.id, quantity: 1 }],
      });
      alert(`Order placed successfully! Order ID: ${order.id}`);
      fetchProducts();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Checkout failed');
    }
  };

  return (
    <div style={{ maxWidth: '1000px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>🛒 Crop Marketplace</h1>

      <Card title="List New Crop Harvest (Farmer)">
        <form onSubmit={handleCreate} style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1rem', alignItems: 'flex-end' }}>
          <Input label="Crop Name" value={name} onChange={(e) => setName(e.target.value)} required />
          <Input label="Price ($/kg)" type="number" step="0.1" value={price} onChange={(e) => setPrice(parseFloat(e.target.value))} required />
          <Input label="Quantity (kg)" type="number" value={quantity} onChange={(e) => setQuantity(parseInt(e.target.value))} required />
          <Button type="submit" style={{ marginBottom: '1rem' }}>
            List Harvest
          </Button>
        </form>
      </Card>

      <h2 style={{ fontSize: '1.4rem', marginTop: '2rem', marginBottom: '1rem' }}>Available Produce</h2>
      {loading ? (
        <p>Loading produce...</p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '1.5rem' }}>
          {products.map((p) => (
            <Card key={p.id} title={p.name} subtitle={`Farmer: ${p.farmerId}`}>
              <p style={{ fontSize: '1.3rem', fontWeight: 700, color: 'var(--accent-green)', margin: '0.5rem 0' }}>
                ${p.price} <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>/ kg</span>
              </p>
              <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                Stock Available: {p.quantity} kg
              </p>
              <Button style={{ width: '100%' }} onClick={() => handleBuy(p)}>
                Buy 1 kg Now
              </Button>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
