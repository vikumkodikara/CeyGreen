import { apiClient } from './client';
import { AuthResponse, LoginRequest, RegisterRequest } from '../types/user';

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const res = await apiClient.post<AuthResponse>('/users/login', data);
  return res.data;
};

export const register = async (data: RegisterRequest): Promise<AuthResponse> => {
  const res = await apiClient.post<AuthResponse>('/users/register', data);
  return res.data;
};
