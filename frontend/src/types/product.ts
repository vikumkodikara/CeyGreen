export type ProductListingStatus = 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK';

export interface Product {
  id: number;
  farmerId: string;
  cropName: string;
  quantity: number;
  unitPrice: number;
  harvestDate: string;
  location: string;
  description?: string | null;
  imageUrl?: string | null;
  createdAt?: string;
  active: boolean;
  status: ProductListingStatus;
}

export interface ProductCreateRequest {
  cropName: string;
  quantity: number;
  unitPrice: number;
  harvestDate: string;
  location: string;
  description?: string;
  imageUrl?: string;
}

export interface ProductUpdateRequest {
  unitPrice?: number;
  quantity?: number;
  active?: boolean;
  description?: string;
  imageUrl?: string;
  location?: string;
}

export interface ProductListParams {
  q?: string;
  cropName?: string;
  location?: string;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  active?: boolean;
  status?: ProductListingStatus;
  sort?: 'price_asc' | 'price_desc' | 'newest';
  page?: number;
  size?: number;
}
