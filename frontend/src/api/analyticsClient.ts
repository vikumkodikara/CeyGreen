import axios from 'axios';

/**
 * Dedicated Axios client for the CeyGreen Sales Analytics & Notification Service
 * running on port 8086.
 *
 * All requests include the X-API-KEY header required by the Spring Security filter.
 */
const ANALYTICS_BASE_URL =
  import.meta.env.VITE_ANALYTICS_API_URL || 'http://localhost:8086';

const ANALYTICS_API_KEY =
  import.meta.env.VITE_ANALYTICS_API_KEY || 'ceygreen-secret-api-key-2026';

export const analyticsClient = axios.create({
  baseURL: ANALYTICS_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'X-API-KEY': ANALYTICS_API_KEY,
  },
  timeout: 10000,
});
