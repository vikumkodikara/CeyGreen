import React from 'react';
import { Product } from '../../types/product';

const cropEmoji: Record<string, string> = {
  tomato: '🍅',
  beans: '🫘',
  carrot: '🥕',
  potato: '🥔',
  default: '🌿',
};

export const ProductImage: React.FC<{ product: Product; className?: string }> = ({
  product,
  className = '',
}) => {
  if (product.imageUrl) {
    return <img src={product.imageUrl} alt={product.cropName} className={`product-image ${className}`} />;
  }
  const key = product.cropName.toLowerCase();
  const emoji = Object.entries(cropEmoji).find(([k]) => key.includes(k))?.[1] ?? cropEmoji.default;
  return (
    <div className={`product-image product-image-placeholder ${className}`} aria-hidden>
      {emoji}
    </div>
  );
};
