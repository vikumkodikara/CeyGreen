import React from 'react';
import { Card } from '../ui/Card';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { ProductCreateRequest } from '../../types/product';

interface FarmerListingFormProps {
  value: ProductCreateRequest;
  loading?: boolean;
  onChange: (value: ProductCreateRequest) => void;
  onSubmit: (e: React.FormEvent) => void;
}

export const FarmerListingForm: React.FC<FarmerListingFormProps> = ({
  value,
  loading,
  onChange,
  onSubmit,
}) => (
  <Card title="List New Crop Harvest">
    <form className="marketplace-form-grid" onSubmit={onSubmit}>
      <Input
        label="Crop name"
        value={value.cropName}
        onChange={(e) => onChange({ ...value, cropName: e.target.value })}
        required
      />
      <Input
        label="Unit price (Rs.)"
        type="number"
        step="0.01"
        min="0.01"
        value={value.unitPrice}
        onChange={(e) =>
          onChange({
            ...value,
            unitPrice: Number.isNaN(e.currentTarget.valueAsNumber)
              ? value.unitPrice
              : e.currentTarget.valueAsNumber,
          })
        }
        required
      />
      <Input
        label="Quantity (kg)"
        type="number"
        min="1"
        value={value.quantity}
        onChange={(e) =>
          onChange({
            ...value,
            quantity: Number.isNaN(e.currentTarget.valueAsNumber)
              ? value.quantity
              : e.currentTarget.valueAsNumber,
          })
        }
        required
      />
      <Input
        label="Harvest date"
        type="date"
        value={value.harvestDate}
        onChange={(e) => onChange({ ...value, harvestDate: e.target.value })}
        required
      />
      <Input
        label="Location"
        value={value.location}
        onChange={(e) => onChange({ ...value, location: e.target.value })}
        required
      />
      <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
        List Harvest
      </Button>
    </form>
  </Card>
);
