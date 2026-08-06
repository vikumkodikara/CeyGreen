export interface Zone {
  zoneId: string;
  zoneName: string;
  cropType: string;
  thresholds?: Record<string, number>;
}

export interface Greenhouse {
  id: string;
  name: string;
  farmerId: string;
  createdAt: string;
  zones?: Zone[];
}

export interface Suggestion {
  zoneId: string;
  message: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  resolved: boolean;
  timestamp: string;
}

export interface ReadingRequest {
  greenhouseId: string;
  zoneId: string;
  temperature: number;
  humidity: number;
  soilMoisture: number;
  nitrogen: number;
  phosphorus: number;
  potassium: number;
}
