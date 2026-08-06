import { apiClient } from './client';
import { Greenhouse, ReadingRequest, Suggestion } from '../types/iot';

export const registerGreenhouse = async (name: string, farmerId: string): Promise<{ id: string }> => {
  const res = await apiClient.post<{ id: string }>('/iot/greenhouses', { name, farmerId });
  return res.data;
};

export const ingestReading = async (data: ReadingRequest): Promise<void> => {
  await apiClient.post('/iot/readings', data);
};

export const getSuggestions = async (greenhouseId: string): Promise<{ greenhouseId: string; suggestions: Suggestion[] }> => {
  const res = await apiClient.get<{ greenhouseId: string; suggestions: Suggestion[] }>(`/iot/suggestions/${greenhouseId}`);
  return res.data;
};

export const updateThresholds = async (zoneId: string, thresholds: Record<string, number>): Promise<void> => {
  await apiClient.put(`/iot/thresholds/${zoneId}`, { thresholds });
};
