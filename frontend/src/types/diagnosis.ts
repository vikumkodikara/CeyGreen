export interface Diagnosis {
  id: string;
  farmerId: string;
  cropType: string;
  imageUrl: string;
  predictedDisease: string;
  confidence: number;
  isUncertain: boolean;
  createdAt: string;
}

export interface DiagnosisUploadResponse {
  id: string;
  predictedDisease: string;
  confidence: number;
  isUncertain: boolean;
  imageUrl: string;
}
