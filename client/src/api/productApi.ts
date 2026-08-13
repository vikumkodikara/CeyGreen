import { apiClient } from './client';
import { PageResponse } from '../types/api';
import {
  Product,
  ProductCreateRequest,
  ProductListParams,
  ProductUpdateRequest,
} from '../types/product';

const buildParams = (params: ProductListParams = {}) => {
  const query: Record<string, string | number | boolean> = {};
  if (params.q) query.q = params.q;
  if (params.cropName) query.cropName = params.cropName;
  if (params.location) query.location = params.location;
  if (params.minPrice != null) query.minPrice = params.minPrice;
  if (params.maxPrice != null) query.maxPrice = params.maxPrice;
  if (params.inStock != null) query.inStock = params.inStock;
  if (params.active != null) query.active = params.active;
  if (params.status) query.status = params.status;
  if (params.sort) query.sort = params.sort;
  if (params.page != null) query.page = params.page;
  if (params.size != null) query.size = params.size;
  return query;
};

export const listProducts = async (params: ProductListParams = {}): Promise<PageResponse<Product>> => {
  const res = await apiClient.get<PageResponse<Product>>('/products', { params: buildParams(params) });
  return res.data;
};

export const listFeaturedProducts = async (limit = 8): Promise<Product[]> => {
  const res = await apiClient.get<Product[]>('/products/featured', { params: { limit } });
  return res.data;
};

export const listCategories = async (): Promise<string[]> => {
  const res = await apiClient.get<string[]>('/products/categories');
  return res.data;
};

export const listLowStockProducts = async (): Promise<Product[]> => {
  const res = await apiClient.get<Product[]>('/products/low-stock');
  return res.data;
};

export const listFarmerProducts = async (
  farmerId: string,
  params: ProductListParams = {}
): Promise<PageResponse<Product>> => {
  const res = await apiClient.get<PageResponse<Product>>(`/products/farmer/${farmerId}`, {
    params: buildParams(params),
  });
  return res.data;
};

export const getProduct = async (id: number): Promise<Product> => {
  const res = await apiClient.get<Product>(`/products/${id}`);
  return res.data;
};

export const createProduct = async (data: ProductCreateRequest): Promise<Product> => {
  const res = await apiClient.post<Product>('/products', data);
  return res.data;
};

export const updateProduct = async (id: number, data: ProductUpdateRequest): Promise<Product> => {
  const res = await apiClient.put<Product>(`/products/${id}`, data);
  return res.data;
};

export const deleteProduct = async (id: number): Promise<void> => {
  await apiClient.delete(`/products/${id}`);
};

export const updateProductStock = async (id: number, quantity: number): Promise<Product> => {
  const res = await apiClient.patch<Product>(`/products/${id}/stock`, { quantity });
  return res.data;
};

export const updateProductStatus = async (id: number, active: boolean): Promise<Product> => {
  const res = await apiClient.patch<Product>(`/products/${id}/status`, { active });
  return res.data;
};
