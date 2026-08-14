export type Role = 'FARMER' | 'BUYER' | 'ADMIN';

export interface User {
  id: string;
  email: string;
  name: string;
  fullName?: string;
  role: Role;
  farmerId?: string;
  buyerId?: string;
  farmLocation?: string;
  contactInfo?: string;
  createdAt?: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  user_id: string;
  role: Role;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: Role;
  farmLocation?: string;
  contactInfo?: string;
}
