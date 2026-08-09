export interface Product {
  id: number;
  farmerId: string;
  cropName: string;
  quantity: number;
  unitPrice: number;
  harvestDate: string;
  location: string;
  active: boolean;
}

export interface ProductCreateRequest {
  cropName: string;
  quantity: number;
  unitPrice: number;
  harvestDate: string;
  location: string;
}

export interface ProductUpdateRequest {
  unitPrice?: number;
  quantity?: number;
  active?: boolean;
}
