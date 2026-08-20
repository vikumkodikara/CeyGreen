import React, { useEffect, useState } from 'react';
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

function emojiForCrop(cropName: string): string {
  const key = cropName.toLowerCase();
  return Object.entries(cropEmoji).find(([k]) => key.includes(k))?.[1] ?? cropEmoji.default;
}

export const ProductImage: React.FC<{ product: Product; className?: string }> = ({
  product,
  className = '',
}) => {
  const [failed, setFailed] = useState(false);
  const src = product.imageUrl || resolveLocalImage(product.cropName);

  useEffect(() => {
    setFailed(false);
  }, [src]);

  if (src && !failed) {
    return (
      <img
        src={src}
        alt={product.cropName}
        className={`product-image ${className}`}
        loading="lazy"
        decoding="async"
        onError={() => setFailed(true)}
      />
    );
  }

  return (
    <div className={`product-image product-image-placeholder ${className}`} aria-hidden>
      {emojiForCrop(product.cropName)}
    </div>
  );
};
