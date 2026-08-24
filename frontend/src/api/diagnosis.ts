import { apiClient } from './client';
import { Diagnosis, DiagnosisSummary, DiagnosisUploadResponse, PaginatedResponse } from '../types/diagnosis';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

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

export const getDiagnosisHistoryPaged = async (
  farmerId: string,
  page = 0,
  size = 12
): Promise<PaginatedResponse<DiagnosisSummary>> => {
  const res = await apiClient.get<PaginatedResponse<DiagnosisSummary>>(
    `/diagnosis/history/${farmerId}/paged`,
    {
      params: {
        page,
        size,
        sort: 'timestamp,desc',
      },
    }
  );
  return res.data;
};

export const deleteDiagnosis = async (id: string): Promise<void> => {
  await apiClient.delete(`/diagnosis/${id}`);
};

export const resolveDiagnosisImageUrl = (imageUrl: string | undefined): string => {
  if (!imageUrl) return '/dashboard/greenhouse.jpg';
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://') || imageUrl.startsWith('data:')) {
    return imageUrl;
  }
  // Strip leading slash if needed and prepend API_BASE_URL if not already present
  const cleanPath = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`;
  if (cleanPath.startsWith('/api/')) {
    return cleanPath;
  }
  const base = API_BASE_URL.endsWith('/') ? API_BASE_URL.slice(0, -1) : API_BASE_URL;
  return `${base}${cleanPath}`;
};


