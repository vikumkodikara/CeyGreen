import axios from 'axios';

/**
 * Axios client for Sales Analytics and Notification services routed via API Gateway.
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export const analyticsClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

analyticsClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ceygreen_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers['X-API-Key'] =
    import.meta.env.VITE_API_KEY || 'ceygreen-dev-api-key';
  return config;
});

