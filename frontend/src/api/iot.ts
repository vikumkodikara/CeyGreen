import { apiClient } from './client';
import { Greenhouse, LiveReading, ReadingRequest, Suggestion } from '../types/iot';

export const registerGreenhouse = async (
  name: string,
  farmerId: string,
  greenhouseId?: string
): Promise<Greenhouse> => {
  const res = await apiClient.post<Greenhouse>('/iot/greenhouses', {
    name,
    farmerId,
    greenhouseId,
    zones: [
      {
        zoneId: 'ZONE1',
        zoneName: 'Zone 1',
        cropType: 'Tomato',
      },
    ],
  });
  return res.data;
};

export const unregisterGreenhouse = async (
  greenhouseId: string,
  farmerId: string
): Promise<void> => {
  await apiClient.delete(`/iot/greenhouses/${encodeURIComponent(greenhouseId)}`, {
    params: { farmerId },
  });
};

export const listMyGreenhouses = async (farmerId: string): Promise<Greenhouse[]> => {
  const res = await apiClient.get<Greenhouse[]>('/iot/greenhouses', {
    params: { farmerId },
  });
  return Array.isArray(res.data) ? res.data : [];
};

export const ingestReading = async (data: ReadingRequest): Promise<void> => {
  await apiClient.post('/iot/readings', {
    greenhouseId: data.greenhouseId,
    zoneId: data.zoneId,
    temperature: data.temperature,
    humidity: data.humidity,
    soilMoisture: data.soilMoisture,
    n: data.nitrogen,
    p: data.phosphorus,
    k: data.potassium,
  });
};

export const getLatestReading = async (
  greenhouseId: string,
  farmerId: string
): Promise<LiveReading> => {
  const res = await apiClient.get<LiveReading>(`/iot/readings/${greenhouseId}/latest`, {
    params: { farmerId },
  });
  return res.data;
};

export const getSuggestions = async (
  greenhouseId: string,
  farmerId: string
): Promise<Suggestion[]> => {
  const res = await apiClient.get<Suggestion[]>(`/iot/suggestions/${greenhouseId}`, {
    params: { farmerId },
  });
  return res.data;
};

export const updateThresholds = async (
  zoneId: string,
  greenhouseId: string,
  farmerId: string,
  thresholds: {
    maxTemperature: number;
    urgentMaxTemperature: number;
    minTemperature: number;
    minSoilMoisture: number;
    maxHumidity: number;
    minNitrogen: number;
    minPhosphorus: number;
    minPotassium: number;
  }
): Promise<void> => {
  const actualThresholds = typeof farmerId === 'object' ? farmerId : thresholds;
  const actualFarmerId = typeof farmerId === 'string' ? farmerId : undefined;
  await apiClient.put(`/iot/thresholds/${zoneId}`, {
    greenhouseId,
    farmerId,
    ...thresholds,
  });
};
