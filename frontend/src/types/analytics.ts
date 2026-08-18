export interface SalesSummary {
  farmerId: string;
  totalRevenue: number;
  totalOrders: number;
  lastUpdated: string;
}

export interface TrendPoint {
  date: string;
  revenue: number;
  orders: number;
}

export interface SalesTrend {
  farmerId: string;
  trend: TrendPoint[];
}

export interface LeaderboardEntry {
  rank: number;
  farmerId: string;
  totalRevenue: number;
  totalOrders: number;
  lastUpdated: string;
}

// The backend returns a plain LeaderboardEntry[] array (not a wrapped object)
export type LeaderboardResponse = LeaderboardEntry[];
