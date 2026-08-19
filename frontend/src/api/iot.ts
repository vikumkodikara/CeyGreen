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

export const getLatestReading = async (greenhouseId: string): Promise<LiveReading> => {
  const res = await apiClient.get<LiveReading>(`/iot/readings/${greenhouseId}/latest`);
  return res.data;
};

export const getSuggestions = async (greenhouseId: string): Promise<Suggestion[]> => {
  const res = await apiClient.get<Suggestion[]>(`/iot/suggestions/${greenhouseId}`);
  return res.data;
};

export const updateThresholds = async (
  zoneId: string,
  greenhouseId: string,
  thresholds: {
    maxTemperature: number;
    urgentMaxTemperature: number;
    minTemperature: number;
    minSoilMoisture: number;
    urgentMinSoilMoisture: number;
    maxSoilMoisture: number;
    maxHumidity: number;
    minHumidity: number;
    minNitrogen: number;
    minPhosphorus: number;
    minPotassium: number;
  }
): Promise<void> => {
  await apiClient.put(`/iot/thresholds/${zoneId}`, {
    greenhouseId,
    ...thresholds,
  });
};
