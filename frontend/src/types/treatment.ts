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
