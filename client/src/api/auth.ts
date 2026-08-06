import { apiClient } from './client';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/user';

export const login = async (data: LoginRequest): Promise<{ token: AuthResponse; user: User }> => {
  const res = await apiClient.post<AuthResponse>('/users/login', data);
  const token = res.data;
  
  const userName = data.email.split('@')[0];
  const user: User = {
    id: token.user_id,
    email: data.email,
    name: userName,
    fullName: userName,
    role: token.role,
    farmerId: token.role === 'FARMER' ? token.user_id : undefined,
    buyerId: token.role === 'BUYER' ? token.user_id : undefined,
  };
  
  return { token, user };
};

export const register = async (data: RegisterRequest): Promise<User> => {
  const res = await apiClient.post<User>('/users/register', data);
  return res.data;
};
