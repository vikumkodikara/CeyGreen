import React from 'react';
import { CROPS_LIST } from '../../data/diseaseKnowledge';
import { CropCard } from './CropCard';

interface CropSelectorProps {
  selectedCrop: string;
  onSelectCrop: (cropId: string) => void;
}

export const CropSelector: React.FC<CropSelectorProps> = ({
  selectedCrop,
  onSelectCrop,
}) => {
  return (
    <div className="crop-selector-ribbon">
      <div className="crop-cards-grid">
        {CROPS_LIST.map((crop) => (
          <CropCard
            key={crop.id}
            id={crop.id}
            name={crop.name}
            image={crop.image}
            isSelected={selectedCrop === crop.id}
            onSelect={onSelectCrop}
          />
        ))}
      </div>
    </div>
  );
};
