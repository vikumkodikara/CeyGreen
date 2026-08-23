export interface Diagnosis {
  id?: string;
  diagnosisId?: string;
  farmerId: string;
  cropType: string;
  imageUrl: string;
  predictedDisease: string;
  confidenceScore?: number;
  confidence?: number;
  isUncertain?: boolean;
  timestamp?: string;
  createdAt?: string;
}

export interface DiagnosisSummary {
  diagnosisId: string;
  cropType: string;
  predictedDisease: string;
  confidenceScore: number;
  imageUrl: string;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface DiagnosisUploadResponse {
  id?: string;
  diagnosisId?: string;
  predictedDisease: string;
  confidence?: number;
  confidenceScore?: number;
  isUncertain?: boolean;
  imageUrl: string;
  cropType?: string;
  timestamp?: string;
}

