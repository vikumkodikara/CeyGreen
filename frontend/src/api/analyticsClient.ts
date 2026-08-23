import axios from 'axios';

/**
 * Axios client for Sales Analytics and Notification services (Student 6).
 * Uses dynamic backend URL resolution or relative /api/analytics path for deployment/Nginx.
 */
const API_BASE =
  import.meta.env.VITE_ANALYTICS_URL ||
  import.meta.env.VITE_ANALYTICS_API_URL ||
  import.meta.env.VITE_API_BASE_URL ||
  '/api/analytics';

export const analyticsClient = axios.create({
  baseURL: API_BASE,
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
