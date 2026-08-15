import React, { useEffect, useRef, useState } from 'react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { getLatestReading, getSuggestions, registerGreenhouse } from '../api/iot';
import { useAuth } from '../hooks/useAuth';
import { LiveReading, Suggestion } from '../types/iot';
import { PageHeader } from '../components/layout/PageHeader';
import { SensorMeter } from '../components/iot/SensorMeter';
import { IconDrop, IconSun, IconThermo } from '../components/icons/Icons';
import './GreenhousePage.css';

type History = Record<string, number[]>;

function pushSample(prev: History, key: string, value: number): number[] {
  const next = [...(prev[key] || []), value].slice(-12);
  return next;
}

export const GreenhousePage: React.FC = () => {
  const { user } = useAuth();
  const [ghName, setGhName] = useState('Greenhouse Alpha');
  const [requestedId, setRequestedId] = useState('GH001');
  const [greenhouseId, setGreenhouseId] = useState('');
  const [loading, setLoading] = useState(false);
  const [live, setLive] = useState<LiveReading | null>(null);
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [liveError, setLiveError] = useState('');
  const [history, setHistory] = useState<History>({});
  const inFlight = useRef(false);
  const lastStamp = useRef('');

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const id = requestedId.trim() || 'GH001';
    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-001';
      const res = await registerGreenhouse(ghName, farmerId, id);
      setGreenhouseId(res.id);
    } catch (err: any) {
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
    let ticks = 0;

    const tick = async () => {
      if (inFlight.current) return;
      inFlight.current = true;
      try {
        const reading = await getLatestReading(greenhouseId).catch((err) => {
          if (err.response?.status === 400) return null;
          throw err;
        });
        if (cancelled) return;
        if (reading) {
          setLive(reading);
          setLiveError('');
          if (reading.timestamp !== lastStamp.current) {
            lastStamp.current = reading.timestamp;
            setHistory((prev) => ({
              t: pushSample(prev, 't', reading.temperature),
              h: pushSample(prev, 'h', reading.humidity),
              s: pushSample(prev, 's', reading.soilMoisture),
              n: pushSample(prev, 'n', reading.n),
              p: pushSample(prev, 'p', reading.p),
              k: pushSample(prev, 'k', reading.k),
            }));
          }
        } else {
          setLiveError('Waiting for the ESP32 to send a reading…');
        }

        ticks += 1;
        if (ticks === 1 || ticks % 5 === 0) {
          const list = await getSuggestions(greenhouseId).catch(() => [] as Suggestion[]);
          if (!cancelled) setSuggestions(list);
        }
      } catch (err: any) {
        if (!cancelled) {
          setLiveError(err.response?.data?.message || 'Could not load live data');
        }
      } finally {
        inFlight.current = false;
      }
    };

    tick();
    const timer = window.setInterval(tick, 1000);
    return () => {
      cancelled = true;
      window.clearInterval(timer);
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
          <form onSubmit={handleRegister} className="row">
            <div className="grow">
              <Input label="Greenhouse name" value={ghName} onChange={(e) => setGhName(e.target.value)} required />
            </div>
            <div className="grow">
              <Input
                label="Greenhouse ID"
                value={requestedId}
                onChange={(e) => setRequestedId(e.target.value.toUpperCase())}
                required
              />
            </div>
            <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
              Register
            </Button>
          </form>
          {greenhouseId && (
            <p className="alert alert-success" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
              Live house {greenhouseId} · ZONE1 · refresh 1s
            </p>
          )}
        </Card>

        {greenhouseId && (
          <Card title="Live ESP32 reading">
            {live ? (
              <>
                <div className="live-head">
                  <p className="page-subtitle">
                    Last update {new Date(live.timestamp).toLocaleTimeString()} · {live.zoneId}
                  </p>
                  <span className="live-dot"><i /> LIVE</span>
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
                    history={history.t || []}
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
                    history={history.h || []}
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
                    history={history.s || []}
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
                    history={history.n || []}
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
                    history={history.p || []}
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
                    history={history.k || []}
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
              <p className="page-subtitle">No alerts right now. Values in range, or no reading yet.</p>
            ) : (
              <ul className="suggest-list">
                {suggestions.map((s, index) => (
                  <li key={`${s.zoneId}-${index}`}>
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
