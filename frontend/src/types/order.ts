export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface Order {
  id: number;
  buyerId: string;
  productId: number;
  farmerId?: string;
  cropName?: string;
  quantity: number;
  unitPrice?: number;
  totalPrice: number;
  status: OrderStatus;
  orderedAt: string;
  buyerName?: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
}

export interface CheckoutItem {
  productId: number;
  quantity: number;
}

export interface CheckoutRequest {
  items?: CheckoutItem[];
  productId?: number;
  quantity?: number;
  buyerName: string;
  phone: string;
  address: string;
  city: string;
  postalCode: string;
}

export interface CheckoutResponse {
  orders: Order[];
}

export interface OrderListParams {
  status?: OrderStatus;
  page?: number;
  size?: number;
  sort?: string;
}
