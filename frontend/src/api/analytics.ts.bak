import { apiClient } from './client';
import { LeaderboardResponse, SalesSummary, SalesTrend } from '../types/analytics';

export const getSalesSummary = async (farmerId: string): Promise<SalesSummary> => {
  const res = await apiClient.get<SalesSummary>(`/analytics/sales/${farmerId}`);
  return res.data;
};

export const getSalesTrend = async (farmerId: string): Promise<SalesTrend> => {
  const res = await apiClient.get<SalesTrend>(`/analytics/sales/${farmerId}/trend`);
  return res.data;
};

export const getLeaderboard = async (): Promise<LeaderboardResponse> => {
  const res = await apiClient.get<LeaderboardResponse>('/analytics/leaderboard');
  return res.data;
};
