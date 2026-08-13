export interface Treatment {
  id: number;
  diseaseName: string;
  productName: string;
  type: 'CHEMICAL' | 'ORGANIC';
  dosage: string;
  frequency: string;
  safetyNotes: string;
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
