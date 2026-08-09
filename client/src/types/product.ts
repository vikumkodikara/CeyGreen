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
