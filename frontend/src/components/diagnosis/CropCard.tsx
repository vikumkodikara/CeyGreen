import React from 'react';

interface CropCardProps {
  id: string;
  name: string;
  image: string;
  isSelected: boolean;
  onSelect: (id: string) => void;
}

export const CropCard: React.FC<CropCardProps> = ({
  id,
  name,
  image,
  isSelected,
  onSelect,
}) => {
  return (
    <button
      type="button"
      className={`crop-card ${isSelected ? 'active' : ''}`}
      onClick={() => onSelect(id)}
      aria-label={`Select ${name}`}
    >
      <div className="crop-card-img-wrap">
        <img src={image} alt={name} className="crop-card-img" />
      </div>
      <span className="crop-card-name">{name}</span>
    </button>
  );
};
