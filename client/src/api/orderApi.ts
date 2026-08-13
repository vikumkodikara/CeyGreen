import { apiClient } from './client';
import { PageResponse } from '../types/api';
import {
  CheckoutRequest,
  CheckoutResponse,
  Order,
  OrderListParams,
  OrderStatus,
} from '../types/order';

export const checkout = async (data: CheckoutRequest): Promise<CheckoutResponse> => {
  const res = await apiClient.post<CheckoutResponse>('/orders/checkout', data);
  return res.data;
};

export const getOrder = async (id: number): Promise<Order> => {
  const res = await apiClient.get<Order>(`/orders/${id}`);
  return res.data;
};

export const getMyOrders = async (params: OrderListParams = {}): Promise<PageResponse<Order>> => {
  const res = await apiClient.get<PageResponse<Order>>('/orders/my-orders', { params });
  return res.data;
};

export const getFarmerOrders = async (params: OrderListParams = {}): Promise<PageResponse<Order>> => {
  const res = await apiClient.get<PageResponse<Order>>('/orders/farmer', { params });
  return res.data;
};

export const updateOrderStatus = async (id: number, status: OrderStatus): Promise<Order> => {
  const res = await apiClient.patch<Order>(`/orders/${id}/status`, { status });
  return res.data;
};
