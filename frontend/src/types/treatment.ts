export interface RatingResponse {
  farmerId: string;
  farmerName: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface Treatment {
  id: number;
  diseaseName: string;
  productName: string;
  type: 'CHEMICAL' | 'ORGANIC';
  dosage: string;
  frequency: string;
  safetyNotes: string;
  phiDays?: number;
  applicationMethod?: string;
  brandNames?: string;
  effectivenessScore?: number;
  averageRating?: number;
  reviews?: RatingResponse[];
  active: boolean;
}

export interface TreatmentRequest {
  diseaseName: string;
  productName: string;
  type: string;
  dosage?: string;
  frequency?: string;
  safetyNotes?: string;
}
