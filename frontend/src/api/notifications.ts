import { apiClient } from './client';

export interface NotificationItem {
  id: number;
  userId: string;
  sourceTopic?: string;
  type?: string;
  channel?: string;
  message: string;
  sentAt?: string;
  createdAt?: string;
  read?: boolean;
  status?: string;
}

/**
 * GET /api/notify/history/{userId}
 * Fetches all notification history for a user via the API gateway.
 */
export const getNotificationHistory = async (userId: string): Promise<NotificationItem[]> => {
  const res = await apiClient.get<NotificationItem[]>(`/notify/history/${userId}`);
  return res.data;
};
