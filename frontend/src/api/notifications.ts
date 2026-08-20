import { analyticsClient } from './analyticsClient';

export interface NotificationItem {
  id: number;
  userId: string;
  sourceTopic: string;
  channel: string;
  message: string;
  sentAt: string;
  status: string;
}

/**
 * GET /notify/history/{userId}
 * Fetches all notification history for a user from the
 * CeyGreen Sales Analytics & Notification Service (port 8086).
 */
export const getNotificationHistory = async (userId: string): Promise<NotificationItem[]> => {
  const res = await analyticsClient.get<NotificationItem[]>(`/notify/history/${userId}`);
  return res.data;
};
