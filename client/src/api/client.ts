import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('ceygreen_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // IoT service (and other services) require the shared gateway API key.
    config.headers['X-API-Key'] =
      import.meta.env.VITE_API_KEY || 'ceygreen-dev-api-key';
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('ceygreen_token');
      localStorage.removeItem('ceygreen_user');
    }
    return Promise.reject(error);
  }
);
