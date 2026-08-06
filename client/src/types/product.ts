export interface Product {
  id: number;
  name: string;
  description: string;
  farmerId: string;
  price: number;
  quantity: number;
  cropType: string;
  available: boolean;
  createdAt: string;
}

export interface ProductRequest {
  name: string;
  description?: string;
  farmerId: string;
  price: number;
  quantity: number;
  cropType?: string;
}

export interface CheckoutItem {
  productId: number;
  quantity: number;
}

export interface CheckoutRequest {
  buyerId: string;
  items: CheckoutItem[];
}

export interface OrderResponse {
  id: number;
  buyerId: string;
  totalAmount: number;
  status: string;
  createdAt: string;
}
