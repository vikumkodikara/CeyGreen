import { apiClient } from './client';
import { DashboardSummary } from '../types/dashboard';

export const getDashboardSummary = async (): Promise<DashboardSummary> => {
  const res = await apiClient.get<DashboardSummary>('/dashboard/summary');
  return res.data;
};
