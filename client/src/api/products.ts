import { apiClient } from './client';
import { Product, ProductCreateRequest, ProductUpdateRequest } from '../types/product';

export const listProducts = async (cropName?: string, location?: string): Promise<Product[]> => {
  const params: Record<string, string> = {};
  if (cropName?.trim()) params.cropName = cropName.trim();
  if (location?.trim()) params.location = location.trim();
  const res = await apiClient.get<Product[]>('/products', { params });
  return res.data;
};

export const getProduct = async (id: number): Promise<Product> => {
  const res = await apiClient.get<Product>(/products/ + id);
  return res.data;
};

export const createProduct = async (data: ProductCreateRequest): Promise<Product> => {
  const res = await apiClient.post<Product>('/products', data);
  return res.data;
};

export const updateProduct = async (id: number, data: ProductUpdateRequest): Promise<Product> => {
  const res = await apiClient.put<Product>(/products/ + id, data);
  return res.data;
};
