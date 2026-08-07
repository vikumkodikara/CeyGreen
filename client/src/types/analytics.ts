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
  farmerId: string;
  totalRevenue: number;
  totalOrders: number;
  rank: number;
}

export interface LeaderboardResponse {
  farmers: LeaderboardEntry[];
}
