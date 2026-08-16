import React, { useState } from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { registerGreenhouse, ingestReading, getSuggestions } from '../api/iot';
import { useAuth } from '../hooks/useAuth';
import { Suggestion } from '../types/iot';
import { PageHeader } from '../components/layout/PageHeader';

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
    <div className="page-wrap">
      <PageHeader
        title="Greenhouse"
        subtitle="Register a zone, push a reading, and review climate suggestions."
      />

      <div className="stack">
        <Card title="Register greenhouse" subtitle="Creates one zone with an ESP32 device">
          <form onSubmit={handleRegister} className="row">
            <div className="grow">
              <Input label="Greenhouse name" value={ghName} onChange={(e) => setGhName(e.target.value)} required />
            </div>
            <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
              Register
            </Button>
          </form>
          {greenhouseId && (
            <p className="alert alert-success" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
              Registered ID: {greenhouseId} · ZONE1
            </p>
          )}
        </Card>

        {greenhouseId && (
          <Card title="Simulated ESP32 reading">
            <form onSubmit={handleIngest}>
              <div className="sensor-grid">
                <Input label="Temperature (°C)" type="number" step="0.1" value={temp} onChange={(e) => setTemp(parseFloat(e.target.value))} />
                <Input label="Humidity (%)" type="number" step="0.1" value={humidity} onChange={(e) => setHumidity(parseFloat(e.target.value))} />
                <Input label="Soil moisture (%)" type="number" step="0.1" value={soilMoisture} onChange={(e) => setSoilMoisture(parseFloat(e.target.value))} />
                <Input label="Nitrogen (N)" type="number" value={nitrogen} onChange={(e) => setNitrogen(parseInt(e.target.value))} />
                <Input label="Phosphorus (P)" type="number" value={phosphorus} onChange={(e) => setPhosphorus(parseInt(e.target.value))} />
                <Input label="Potassium (K)" type="number" value={potassium} onChange={(e) => setPotassium(parseInt(e.target.value))} />
              </div>
              <Button type="submit" style={{ marginTop: '1rem' }}>
                Push reading
              </Button>
            </form>
          </Card>
        )}

        {suggestions.length > 0 && (
          <Card title="Rule engine suggestions">
            <ul style={{ margin: 0, paddingLeft: '1.2rem' }}>
              {suggestions.map((s, index) => (
                <li key={`${s.zoneId}-${index}`} style={{ marginBottom: '0.55rem' }}>
                  <span className="pill">{s.severity}</span> {s.message}
                </li>
              ))}
            </ul>
          </Card>
        )}
      </div>
    </div>
  );
};
