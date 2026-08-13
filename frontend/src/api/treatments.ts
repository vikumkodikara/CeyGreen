import { apiClient } from './client';
import { Treatment, TreatmentRequest } from '../types/treatment';

export const getTreatmentsForDisease = async (diseaseName: string): Promise<Treatment[]> => {
  const res = await apiClient.get<Treatment[]>(`/treatments/${encodeURIComponent(diseaseName)}`);
  return res.data;
};

export const searchTreatments = async (crop?: string, severity?: string): Promise<Treatment[]> => {
  const params: Record<string, string> = {};
  if (crop) params.crop = crop;
  if (severity) params.severity = severity;

  const res = await apiClient.get<Treatment[]>('/treatments/search', { params });
  return res.data;
};

export const createTreatment = async (data: TreatmentRequest): Promise<Treatment> => {
  const res = await apiClient.post<Treatment>('/treatments', data);
  return res.data;
};
