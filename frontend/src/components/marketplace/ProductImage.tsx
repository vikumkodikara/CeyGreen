import React from 'react';
import { Product } from '../../types/product';

const cropEmoji: Record<string, string> = {
  tomato: '🍅',
  potato: '🥔',
  pepper: '🫑',
  strawberry: '🍓',
  grape: '🍇',
  beans: '🫘',
  carrot: '🥕',
  default: '🌿',
};

const localImageByCrop: Record<string, string> = {
  tomato: '/marketplace/tomato.webp',
  potato: '/marketplace/potato.webp',
  pepper: '/marketplace/bell-pepper.webp',
  'bell pepper': '/marketplace/bell-pepper.webp',
  strawberry: '/marketplace/strawberry.webp',
  grape: '/marketplace/grapes.webp',
  grapes: '/marketplace/grapes.webp',
};

function resolveLocalImage(cropName: string): string | undefined {
  const key = cropName.toLowerCase();
  return Object.entries(localImageByCrop).find(([k]) => key.includes(k))?.[1];
}

export const ProductImage: React.FC<{ product: Product; className?: string }> = ({
  product,
  className = '',
}) => {
  const src = product.imageUrl || resolveLocalImage(product.cropName);

  if (src) {
    return (
      <img
        src={src}
        alt={product.cropName}
        className={`product-image ${className}`}
        loading="lazy"
        decoding="async"
      />
    );
  }

  const key = product.cropName.toLowerCase();
  const emoji = Object.entries(cropEmoji).find(([k]) => key.includes(k))?.[1] ?? cropEmoji.default;

  return (
    <div className={`product-image product-image-placeholder ${className}`} aria-hidden>
      {emoji}
    </div>
  );
};
