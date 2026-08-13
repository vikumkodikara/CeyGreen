import React, { useState } from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { registerGreenhouse, ingestReading, getSuggestions } from '../api/iot';
import { useAuth } from '../hooks/useAuth';
import { Suggestion } from '../types/iot';

export const GreenhousePage: React.FC = () => {
  const { user } = useAuth();
  const [ghName, setGhName] = useState('Greenhouse Alpha');
  const [greenhouseId, setGreenhouseId] = useState('');
  const [loading, setLoading] = useState(false);
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);

  const [temp, setTemp] = useState(40);
  const [humidity, setHumidity] = useState(70);
  const [soilMoisture, setSoilMoisture] = useState(35);
  const [nitrogen, setNitrogen] = useState(10);
  const [phosphorus, setPhosphorus] = useState(12);
  const [potassium, setPotassium] = useState(9);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-001';
      const res = await registerGreenhouse(ghName, farmerId);
      setGreenhouseId(res.id);
      setSuggestions([]);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Greenhouse registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleIngest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!greenhouseId) return;
    try {
      await ingestReading({
        greenhouseId,
        zoneId: 'ZONE1',
        temperature: temp,
        humidity,
        soilMoisture,
        nitrogen,
        phosphorus,
        potassium,
      });
      const list = await getSuggestions(greenhouseId);
      setSuggestions(list);
      alert('Sensor reading saved. Rule engine updated suggestions.');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to ingest reading');
    }
  };

  return (
    <div style={{ maxWidth: '900px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>
        Greenhouse IoT Telemetry & Control
      </h1>

      <Card title="1. Register Greenhouse Blueprint (1 zone + 1 ESP32)">
        <form onSubmit={handleRegister} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
          <div style={{ flex: 1 }}>
            <Input
              label="Greenhouse Name"
              value={ghName}
              onChange={(e) => setGhName(e.target.value)}
              required
            />
          </div>
          <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
            Register Blueprint
          </Button>
        </form>
        {greenhouseId && (
          <p style={{ color: 'var(--accent-green)', fontWeight: 600, marginTop: '0.5rem' }}>
            Registered Greenhouse ID: {greenhouseId} (ZONE1)
          </p>
        )}
      </Card>

      {greenhouseId && (
        <Card title="2. Simulated ESP32 Telemetry Reading" style={{ marginTop: '2rem' }}>
          <form onSubmit={handleIngest}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem' }}>
              <Input label="Temperature (°C)" type="number" step="0.1" value={temp} onChange={(e) => setTemp(parseFloat(e.target.value))} />
              <Input label="Humidity (%)" type="number" step="0.1" value={humidity} onChange={(e) => setHumidity(parseFloat(e.target.value))} />
              <Input label="Soil Moisture (%)" type="number" step="0.1" value={soilMoisture} onChange={(e) => setSoilMoisture(parseFloat(e.target.value))} />
              <Input label="Nitrogen (N)" type="number" value={nitrogen} onChange={(e) => setNitrogen(parseInt(e.target.value))} />
              <Input label="Phosphorus (P)" type="number" value={phosphorus} onChange={(e) => setPhosphorus(parseInt(e.target.value))} />
              <Input label="Potassium (K)" type="number" value={potassium} onChange={(e) => setPotassium(parseInt(e.target.value))} />
            </div>
            <Button type="submit" style={{ marginTop: '1rem' }}>
              Push ESP32 Reading
            </Button>
          </form>
        </Card>
      )}

      {suggestions.length > 0 && (
        <Card title="3. Rule Engine Suggestions" style={{ marginTop: '2rem' }}>
          <ul style={{ margin: 0, paddingLeft: '1.2rem' }}>
            {suggestions.map((s, index) => (
              <li key={`${s.zoneId}-${index}`} style={{ marginBottom: '0.5rem' }}>
                <strong>{s.severity}</strong>: {s.message}
              </li>
            ))}
          </ul>
        </Card>
      )}
    </div>
  );
};
