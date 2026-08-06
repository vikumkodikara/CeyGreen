import { apiClient } from './client';
import { CheckoutRequest, OrderResponse, Product, ProductRequest } from '../types/product';

export const listProducts = async (): Promise<Product[]> => {
  const res = await apiClient.get<Product[]>('/products');
  return res.data;
};

export const getProduct = async (id: number): Promise<Product> => {
  const res = await apiClient.get<Product>(`/products/${id}`);
  return res.data;
};

export const createProduct = async (data: ProductRequest): Promise<Product> => {
  const res = await apiClient.post<Product>('/products', data);
  return res.data;
};

export const checkout = async (data: CheckoutRequest): Promise<OrderResponse> => {
  const res = await apiClient.post<OrderResponse>('/orders/checkout', data);
  return res.data;
};
