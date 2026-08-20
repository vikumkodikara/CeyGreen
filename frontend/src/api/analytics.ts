import { analyticsClient } from './analyticsClient';
import { LeaderboardResponse, SalesSummary, SalesTrend } from '../types/analytics';

/**
 * GET /analytics/sales/{farmerId}
 * Returns total orders, total revenue, and last updated timestamp for a farmer.
 */
export const getSalesSummary = async (farmerId: string): Promise<SalesSummary> => {
  const res = await analyticsClient.get<SalesSummary>(`/analytics/sales/${farmerId}`);
  return res.data;
};

/**
 * GET /analytics/sales/{farmerId}/trend
 * Returns sales volume, revenue, average order value, and historical order breakdown.
 */
export const getSalesTrend = async (farmerId: string): Promise<SalesTrend> => {
  const res = await analyticsClient.get<SalesTrend>(`/analytics/sales/${farmerId}/trend`);
  return res.data;
};

/**
 * GET /analytics/leaderboard
 * Returns top farmers ranked by total sales revenue.
 * Backend returns a plain array of LeaderboardEntry objects.
 */
export const getLeaderboard = async (): Promise<LeaderboardResponse> => {
  const res = await analyticsClient.get<LeaderboardResponse>('/analytics/leaderboard');
  return res.data;
};
