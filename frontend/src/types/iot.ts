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
  zone?: string;
  zoneId: string;
  message: string;
  severity: string;
  resolved: boolean;
  createdAt?: string;
}

export interface LiveReading {
  greenhouseId: string;
  zoneId: string;
  timestamp: string;
  temperature: number;
  humidity: number;
  soilMoisture: number;
  n: number;
  p: number;
  k: number;
  status?: string;
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
