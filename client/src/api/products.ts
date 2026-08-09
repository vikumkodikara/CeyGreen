import { apiClient } from './client';
import { Product } from '../types/product';

export const listProducts = async (cropName?: string, location?: string): Promise<Product[]> => {
  const params: Record<string, string> = {};
  if (cropName?.trim()) params.cropName = cropName.trim();
  if (location?.trim()) params.location = location.trim();
  const res = await apiClient.get<Product[]>('/products', { params });
  return res.data;
};
