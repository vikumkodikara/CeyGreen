import React, { useEffect, useMemo, useState } from 'react';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { getLatestReading, registerGreenhouse } from '../api/iot';
import { useAuth } from '../hooks/useAuth';
import { LiveReading } from '../types/iot';
import { evaluateReading } from '../utils/iotRules';
import { PageHeader } from '../components/layout/PageHeader';
import { SensorMeter } from '../components/iot/SensorMeter';
import { IconDrop, IconSun, IconThermo } from '../components/icons/Icons';
import './GreenhousePage.css';

const IOT_ONLY = import.meta.env.VITE_IOT_ONLY === 'true';

function demoReading(id: string): LiveReading {
  return {
    greenhouseId: id,
    zoneId: 'ZONE1',
    timestamp: new Date().toISOString(),
    temperature: 28.4,
    humidity: 72,
    soilMoisture: 41,
    n: 12,
    p: 10,
    k: 11,
    status: 'PREVIEW',
  };
}

export const GreenhousePage: React.FC = () => {
  const { user } = useAuth();
  const [ghName, setGhName] = useState('Greenhouse Alpha');
  const [requestedId, setRequestedId] = useState('GH001');
  const [greenhouseId, setGreenhouseId] = useState(IOT_ONLY ? 'GH001' : '');
  const [loading, setLoading] = useState(false);
  const [live, setLive] = useState<LiveReading | null>(IOT_ONLY ? demoReading('GH001') : null);
  const [liveError, setLiveError] = useState('');
  const suggestions = useMemo(() => (live ? evaluateReading(live) : []), [live]);
  const zoneTone = suggestions.some((s) => s.severity === 'HIGH' || s.severity === 'CRITICAL')
    ? 'alert'
    : suggestions.length
      ? 'watch'
      : 'ok';

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const id = requestedId.trim() || 'GH001';
    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-001';
      const res = await registerGreenhouse(ghName, farmerId, id);
      setGreenhouseId(res.id);
    } catch (err: any) {
      if (IOT_ONLY) {
        setGreenhouseId(id);
        setLive((prev) => prev || demoReading(id));
        return;
      }
      const msg = err.response?.data?.message || 'Greenhouse registration failed';
      if (String(msg).toLowerCase().includes('already exists')) {
        setGreenhouseId(id);
      } else {
        alert(msg);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!greenhouseId) return undefined;
    let cancelled = false;

    const tick = async () => {
      try {
        const reading = await getLatestReading(greenhouseId).catch((err) => {
          if (err.response?.status === 400) return null;
          throw err;
        });
        if (cancelled) return;
        if (reading) {
          setLive(reading);
          setLiveError('');
        } else if (IOT_ONLY) {
          setLive((prev) => prev || demoReading(greenhouseId));
          setLiveError('');
        } else {
          setLiveError('Waiting for the ESP32 to send a reading…');
        }
      } catch (err: any) {
        if (!cancelled) {
          if (IOT_ONLY) {
            setLive((prev) => prev || demoReading(greenhouseId));
            setLiveError('');
          } else {
            setLiveError(err.response?.data?.message || 'Could not load live data');
          }
        }
      }
    };

    const run = async () => {
      while (!cancelled) {
        await tick();
        await new Promise((r) => window.setTimeout(r, 50));
      }
    };
    run();
    return () => {
      cancelled = true;
    };
  }, [greenhouseId]);

  return (
    <div className="page-wrap">
      <PageHeader
        title="Greenhouse"
        subtitle="Register GH001, then this page follows the ESP32 live."
      />

      <div className="stack">
        <Card title="Register greenhouse" subtitle="Creates ZONE1. Use the same id as the ESP32 sketch.">
          <form onSubmit={handleRegister} className="gh-register">
            <div className="gh-register-fields">
              <Input label="Greenhouse name" value={ghName} onChange={(e) => setGhName(e.target.value)} required />
              <Input
                label="Greenhouse ID"
                value={requestedId}
                onChange={(e) => setRequestedId(e.target.value.toUpperCase())}
                required
              />
            </div>
            <button type="submit" className="gh-register-btn" disabled={loading}>
              <span className="gh-register-ico" aria-hidden>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M12 3l2.2 6.4H21l-5.4 3.9 2.1 6.5L12 16.8 6.3 19.8l2.1-6.5L3 9.4h6.8L12 3z" fill="currentColor" />
                </svg>
              </span>
              {loading ? 'Registering…' : 'Register greenhouse'}
            </button>
          </form>
          {greenhouseId && (
            <p className="alert alert-success" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
              Live house {greenhouseId} · ZONE1 · live poll
            </p>
          )}
        </Card>

        {greenhouseId && (
          <Card title="Live ESP32 reading">
            {live ? (
              <>
                <div className="live-head">
                  <p className="page-subtitle">
                    Last update {new Date(live.timestamp).toLocaleTimeString()} · Firebase
                    {' '}greenhouses/{greenhouseId}/zones/{live.zoneId}/readings
                  </p>
                  <span className="live-dot"><i /> {live.status === 'PREVIEW' ? 'PREVIEW' : 'LIVE'}</span>
                </div>
                <div className={`zone-blueprint ${zoneTone}`} aria-label={`Zone ${live.zoneId} ${zoneTone}`}>
                  <svg viewBox="0 0 320 72" className="zone-blueprint-svg">
                    <rect x="8" y="10" width="304" height="52" rx="10" />
                    <text x="160" y="42" textAnchor="middle">{live.zoneId} · {zoneTone === 'alert' ? 'action needed' : zoneTone === 'watch' ? 'watch' : 'healthy'}</text>
                  </svg>
                </div>
                <div className="sensor-grid">
                  <SensorMeter
                    label="Temperature"
                    value={live.temperature}
                    unit="°C"
                    min={10}
                    max={45}
                    color="#e07a3d"
                    hint="Ideal 24–32 °C"
                    icon={<IconThermo />}
                    idealMin={24}
                    idealMax={32}
                  />
                  <SensorMeter
                    label="Humidity"
                    value={live.humidity}
                    unit="%"
                    min={0}
                    max={100}
                    color="#1f8a54"
                    hint="Ideal 60–80%"
                    icon={<IconDrop />}
                    idealMin={60}
                    idealMax={80}
                  />
                  <SensorMeter
                    label="Soil moisture"
                    value={live.soilMoisture}
                    unit="%"
                    min={0}
                    max={100}
                    color="#8b5e34"
                    hint="Ideal 35–60%"
                    icon={<IconDrop />}
                    idealMin={35}
                    idealMax={60}
                  />
                  <SensorMeter
                    label="Nitrogen"
                    value={live.n}
                    unit="N"
                    min={0}
                    max={80}
                    color="#166534"
                    hint="Soil nutrient"
                    icon={<IconSun />}
                    idealMin={10}
                    idealMax={40}
                  />
                  <SensorMeter
                    label="Phosphorus"
                    value={live.p}
                    unit="P"
                    min={0}
                    max={80}
                    color="#b45309"
                    hint="Soil nutrient"
                    icon={<IconSun />}
                    idealMin={8}
                    idealMax={35}
                  />
                  <SensorMeter
                    label="Potassium"
                    value={live.k}
                    unit="K"
                    min={0}
                    max={80}
                    color="#2563eb"
                    hint="Soil nutrient"
                    icon={<IconSun />}
                    idealMin={8}
                    idealMax={35}
                  />
                </div>
              </>
            ) : (
              <p className="page-subtitle">{liveError || 'Waiting for the ESP32…'}</p>
            )}
          </Card>
        )}

        {greenhouseId && (
          <Card title="Rule engine suggestions">
            {suggestions.length === 0 ? (
              <p className="suggest-empty">
                {live
                  ? 'All sensors in range. No action needed.'
                  : 'Suggestions appear after the first ESP32 reading.'}
              </p>
            ) : (
              <ul className="suggest-list">
                {suggestions.map((s, index) => (
                  <li key={`${s.zoneId}-${s.message}-${index}`}>
                    <span className={`pill ${s.severity.toLowerCase()}`}>{s.severity}</span>
                    <span>{s.message}</span>
                  </li>
                ))}
              </ul>
            )}
          </Card>
        )}
      </div>
    </div>
  );
};
