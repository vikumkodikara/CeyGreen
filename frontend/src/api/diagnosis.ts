import { apiClient } from './client';
import { Diagnosis, DiagnosisUploadResponse } from '../types/diagnosis';

export const uploadDiagnosisImage = async (
  file: File,
  farmerId: string,
  cropType: string
): Promise<DiagnosisUploadResponse> => {
  const formData = new FormData();
  formData.append('image', file);
  formData.append('farmerId', farmerId);
  formData.append('cropType', cropType);

  const res = await apiClient.post<DiagnosisUploadResponse>('/diagnosis/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return res.data;
};

export const getDiagnosis = async (id: string): Promise<Diagnosis> => {
  const res = await apiClient.get<Diagnosis>(`/diagnosis/${id}`);
  return res.data;
};

export const getDiagnosisHistory = async (farmerId: string): Promise<Diagnosis[]> => {
  const res = await apiClient.get<Diagnosis[]>(`/diagnosis/history/${farmerId}`);
  return res.data;
};
