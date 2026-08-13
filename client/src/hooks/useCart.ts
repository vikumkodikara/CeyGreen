import { useCallback, useEffect, useState } from 'react';
import { Product } from '../types/product';
import { CartItem, clearCart, loadCart, saveCart } from '../store/cartStorage';

export const useCart = () => {
  const [items, setItems] = useState<CartItem[]>(() => loadCart());

  useEffect(() => {
    saveCart(items);
  }, [items]);

  const addItem = useCallback((product: Product, quantity = 1) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.productId === product.id);
      const nextQty = Math.min(
        (existing?.quantity ?? 0) + quantity,
        product.quantity
      );
      if (nextQty <= 0) return prev;
      const next: CartItem = {
        productId: product.id,
        cropName: product.cropName,
        unitPrice: product.unitPrice,
        quantity: nextQty,
        maxStock: product.quantity,
        imageUrl: product.imageUrl,
        location: product.location,
      };
      if (existing) {
        return prev.map((i) => (i.productId === product.id ? next : i));
      }
      return [...prev, next];
    });
  }, []);

  const removeItem = useCallback((productId: number) => {
    setItems((prev) => prev.filter((i) => i.productId !== productId));
  }, []);

  const updateQuantity = useCallback((productId: number, quantity: number) => {
    setItems((prev) =>
      prev
        .map((i) => {
          if (i.productId !== productId) return i;
          const qty = Math.max(1, Math.min(quantity, i.maxStock));
          return { ...i, quantity: qty };
        })
        .filter((i) => i.quantity > 0)
    );
  }, []);

  const emptyCart = useCallback(() => {
    setItems([]);
    clearCart();
  }, []);

  const subtotal = items.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0);
  const itemCount = items.reduce((sum, i) => sum + i.quantity, 0);

  return { items, addItem, removeItem, updateQuantity, emptyCart, subtotal, itemCount };
};
