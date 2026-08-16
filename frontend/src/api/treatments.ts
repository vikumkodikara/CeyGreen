import { apiClient } from './client';
import { Treatment, TreatmentRequest } from '../types/treatment';

export const getTreatmentsForDisease = async (diseaseName: string): Promise<Treatment[]> => {
  const res = await apiClient.get<Treatment[]>(`/treatments/${encodeURIComponent(diseaseName)}`);
  return res.data;
};

export const searchTreatments = async (crop?: string, severity?: string, type?: string): Promise<Treatment[]> => {
  const params: Record<string, string> = {};
  if (crop) params.crop = crop;
  if (severity) params.severity = severity;
  if (type) params.type = type;

  const res = await apiClient.get<Treatment[]>('/treatments/search', { params });
  return res.data;
};

export const createTreatment = async (data: TreatmentRequest): Promise<Treatment> => {
  const res = await apiClient.post<Treatment>('/treatments', data);
  return res.data;
};

export const getTreatmentsByCrop = async (cropName: string): Promise<Treatment[]> => {
  const res = await apiClient.get<Treatment[]>(`/treatments/crop/${encodeURIComponent(cropName)}`);
  return res.data;
};

export const rateTreatment = async (id: number, farmerId: string, rating: number): Promise<void> => {
  await apiClient.post(`/treatments/${id}/rate`, { farmerId, rating });
};

export const getTreatmentAlternatives = async (id: number): Promise<Treatment[]> => {
  const res = await apiClient.get<Treatment[]>(`/treatments/${id}/alternatives`);
  return res.data;
};
