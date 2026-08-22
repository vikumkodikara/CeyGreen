import React, { useEffect, useMemo, useState } from 'react';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { getLatestReading, getSuggestions, listMyGreenhouses, registerGreenhouse, unregisterGreenhouse } from '../api/iot';
import { useAuth } from '../hooks/useAuth';
import { LiveReading, Suggestion } from '../types/iot';
import { evaluateReading } from '../utils/iotRules';
import {
  clearSavedGreenhouse,
  farmerStorageId,
  readSavedGreenhouseForUser,
  saveGreenhouseForUser,
} from '../utils/greenhouseStorage';
import { PageHeader } from '../components/layout/PageHeader';
import { SensorMeter } from '../components/iot/SensorMeter';
import { SensorLocation } from '../components/iot/SensorLocation';
import { IconBeaker, IconDrop, IconSprout, IconThermo } from '../components/icons/Icons';
import './GreenhousePage.css';

function idleReading(greenhouseId = ''): LiveReading {
  return {
    greenhouseId,
    zoneId: 'ZONE1',
    timestamp: new Date().toISOString(),
    temperature: 0,
    humidity: 0,
    soilMoisture: 0,
    n: 0,
    p: 0,
    k: 0,
    status: 'IDLE',
  };
}

function farmerKey(user: { farmerId?: string; id?: string } | null): string {
  return farmerStorageId(user);
}

function suggestedGreenhouseId(ownerId: string): string {
  const compact = ownerId.replace(/[^a-zA-Z0-9]/g, '').slice(-8).toUpperCase() || 'NEW';
  return `GH-${compact}`;
}

function formatZone(zoneId: string): string {
  const n = zoneId.replace(/^ZONE/i, '').replace(/^Z/i, '');
  return n ? `Zone ${n}` : zoneId;
}

function gaugeState(
  value: number,
  live: boolean,
  idealMin: number,
  idealMax: number
): { status: string; tone: 'ok' | 'watch' | 'alert' | 'idle' } {
  if (!live) return { status: 'Idle', tone: 'idle' };
  if (value < idealMin) return { status: 'Low', tone: 'watch' };
  if (value > idealMax) return { status: 'High', tone: value > idealMax * 1.15 ? 'alert' : 'watch' };
  return { status: 'Normal', tone: 'ok' };
}

function npkState(
  value: number,
  live: boolean,
  min: number
): { status: string; tone: 'ok' | 'watch' | 'alert' | 'idle' } {
  const state = gaugeState(value, live, min, 40);
  if (state.tone === 'ok') return { status: 'Optimal', tone: 'ok' };
  return state;
}

export const GreenhousePage: React.FC = () => {
  const { user } = useAuth();
  const ownerId = farmerKey(user);
  const [ghName, setGhName] = useState('My greenhouse');
  const [requestedId, setRequestedId] = useState('');
  const [greenhouseId, setGreenhouseId] = useState('');
  const [loading, setLoading] = useState(false);
  const [removing, setRemoving] = useState(false);
  const [live, setLive] = useState<LiveReading>(idleReading);
  const [liveError, setLiveError] = useState('');
  const [apiSuggestions, setApiSuggestions] = useState<Suggestion[]>([]);
  const [suggestionsFromApi, setSuggestionsFromApi] = useState(false);
  const localSuggestions = useMemo(
    () => (live.status === 'LIVE' ? evaluateReading(live) : []),
    [live]
  );
  const suggestions = suggestionsFromApi ? apiSuggestions : localSuggestions;

  useEffect(() => {
    if (!ownerId) {
      setGreenhouseId('');
      setRequestedId('');
      setLive(idleReading());
      setApiSuggestions([]);
      setSuggestionsFromApi(false);
      return undefined;
    }

    let cancelled = false;
    const saved = readSavedGreenhouseForUser(user);
    if (saved.id) {
      setGreenhouseId(saved.id);
      setRequestedId(saved.id);
      if (saved.name) setGhName(saved.name);
    }
    setLive(idleReading(saved.id));
    setApiSuggestions([]);
    setSuggestionsFromApi(false);
    setLiveError('');

    const restoreFromApi = async () => {
      try {
        const mine = await listMyGreenhouses(ownerId);
        if (cancelled || !mine.length) return;
        const house = mine[0];
        setGreenhouseId(house.id);
        setRequestedId(house.id);
        if (house.name) setGhName(house.name);
        saveGreenhouseForUser(user, house.id, house.name || saved.name || 'My greenhouse');
      } catch {
        /* keep local restore */
      }
    };
    restoreFromApi();
    return () => {
      cancelled = true;
    };
  }, [ownerId]);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!ownerId) {
      alert('Sign in as a farmer first.');
      return;
    }
    setLoading(true);
    const id = requestedId.trim() || suggestedGreenhouseId(ownerId);
    try {
      const res = await registerGreenhouse(ghName, ownerId, id);
      setGreenhouseId(res.id);
      setRequestedId(res.id);
      setGhName(res.name || ghName);
      saveGreenhouseForUser(user, res.id, res.name || ghName);
      setLive(idleReading(res.id));
      setLiveError('Waiting for the ESP32 to send a reading…');
    } catch (err: any) {
      const status = err.response?.status;
      const msg = err.response?.data?.message || 'Greenhouse registration failed';
      if (status === 409 || String(msg).toLowerCase().includes('another farmer')) {
        alert('That greenhouse ID is already registered to another farmer. Choose a different ID.');
      } else {
        alert(msg);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleUnregister = async () => {
    if (!greenhouseId || !ownerId) return;
    if (!window.confirm('Remove this greenhouse so you can register again for the lecture demo?')) {
      return;
    }
    setRemoving(true);
    try {
      await unregisterGreenhouse(greenhouseId, ownerId);
    } catch (err: any) {
      if (err.response?.status !== 400 && err.response?.status !== 404) {
        alert(err.response?.data?.message || 'Could not remove greenhouse');
        setRemoving(false);
        return;
      }
    }
    clearSavedGreenhouse(ownerId);
    setGreenhouseId('');
    setRequestedId(suggestedGreenhouseId(ownerId));
    setLive(idleReading());
    setLiveError('');
    setApiSuggestions([]);
    setSuggestionsFromApi(false);
    setRemoving(false);
  };

  useEffect(() => {
    if (!greenhouseId || !ownerId) return undefined;
    let cancelled = false;

    const tick = async () => {
      try {
        const reading = await getLatestReading(greenhouseId, ownerId).catch((err) => {
          if (err.response?.status === 400 || err.response?.status === 404) return null;
          if (!err.response) return null;
          throw err;
        });
        if (cancelled) return;
        if (reading) {
          setLive(reading);
          setLiveError('');
          const list = await getSuggestions(greenhouseId, ownerId).catch(() => null);
          if (!cancelled && list) {
            setApiSuggestions(list);
            setSuggestionsFromApi(true);
          }
        } else {
          setLive(idleReading(greenhouseId));
          setLiveError('Waiting for the ESP32 to send a reading…');
          setApiSuggestions([]);
          setSuggestionsFromApi(false);
        }
      } catch (err: any) {
        if (cancelled) return;
        if (err.response?.status === 403) {
          setGreenhouseId('');
          clearSavedGreenhouse(ownerId);
          setLive(idleReading());
          setLiveError('');
          alert('That greenhouse belongs to another farmer.');
          return;
        }
        setLiveError(err.response?.data?.message || 'Could not load live data');
      }
    };

    const run = async () => {
      while (!cancelled) {
        await tick();
        await new Promise((r) => window.setTimeout(r, 2000));
      }
    };
    run();
    return () => {
      cancelled = true;
    };
  }, [greenhouseId, ownerId]);

  return (
    <div className="page-wrap">
      <PageHeader
        title="Greenhouse"
        subtitle="Register once. After login the same house comes back. Meters stay at 0 until the ESP32 sends a reading."
      />

      <div className="stack">
        <Card
          title={greenhouseId ? 'Your greenhouse' : 'Register greenhouse'}
          subtitle={
            greenhouseId
              ? 'Already registered. You do not need to register again after login.'
              : 'Creates ZONE1 for your account only. Use the same ID on the ESP32.'
          }
        >
          {!greenhouseId ? (
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
              <button type="submit" className="gh-register-btn" disabled={loading || !ownerId}>
                {loading ? 'Registering…' : 'Register'}
              </button>
              <p className="page-subtitle" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
                No greenhouse registered yet. Readings stay at 0 until the ESP32 connects.
              </p>
            </form>
          ) : (
            <p className="alert alert-success" style={{ marginTop: 0, marginBottom: '0.85rem' }}>
              Your house <strong>{greenhouseId}</strong> · ZONE1
              <br />
              Farmer ID for Grafana: <strong>{ownerId}</strong>
            </p>
            <button
              type="button"
              className="gh-register-btn gh-register-btn-ghost"
              onClick={handleUnregister}
              disabled={removing}
            >
              {removing ? 'Removing…' : 'Remove greenhouse'}
            </button>
          )}
        </Card>

        <Card title="Live ESP32 reading">
          <div className="live-head">
            <p className="page-subtitle">
              {greenhouseId
                ? live.status === 'LIVE'
                  ? `Last update ${new Date(live.timestamp).toLocaleTimeString()} · ${greenhouseId} / ${formatZone(live.zoneId)}`
                  : `Waiting for ESP32 · ${greenhouseId} / Zone 1 · meters stay at 0`
                : 'Register a greenhouse ID to start live data. Values are 0 until then.'}
            </p>
            <span className="live-dot">
              <i /> {live.status === 'LIVE' ? 'LIVE' : 'IDLE'}
            </span>
          </div>
          {liveError && greenhouseId ? <p className="page-subtitle">{liveError}</p> : null}

          {greenhouseId && (
            <SensorLocation
              greenhouseName={ghName}
              zoneLabel={formatZone(live.zoneId || 'ZONE1')}
              active={live.status === 'LIVE'}
            />
          )}

          <div className="sensor-grid" style={{ marginTop: greenhouseId ? '1rem' : 0 }}>
            <SensorMeter
              label="Temperature"
              value={live.temperature}
              unit="°C"
              min={18}
              max={40}
              color="#f59e0b"
              hint="Ideal 15–30 °C"
              icon={<IconThermo />}
              {...gaugeState(live.temperature, live.status === 'LIVE', 15, 30)}
            />
            <SensorMeter
              label="Humidity"
              value={live.humidity}
              unit="%"
              min={0}
              max={100}
              color="#3b82f6"
              hint="Keep below 90%"
              icon={<IconDrop />}
              {...gaugeState(live.humidity, live.status === 'LIVE', 0, 90)}
            />
            <SensorMeter
              label="Soil moisture"
              value={live.soilMoisture}
              unit="%"
              min={0}
              max={100}
              color="#22c55e"
              hint="Keep above 20%"
              icon={<IconSprout />}
              {...gaugeState(live.soilMoisture, live.status === 'LIVE', 20, 100)}
            />
          </div>

          <p className="npk-heading">NPK nutrients</p>
          <div className="sensor-grid">
            <SensorMeter
              label="Nitrogen"
              value={live.n}
              unit=""
              min={0}
              max={40}
              color="#16a34a"
              hint="N · soil nutrient"
              icon={<IconBeaker />}
              {...npkState(live.n, live.status === 'LIVE', 10)}
            />
            <SensorMeter
              label="Phosphorus"
              value={live.p}
              unit=""
              min={0}
              max={40}
              color="#a855f7"
              hint="P · soil nutrient"
              icon={<IconBeaker />}
              {...npkState(live.p, live.status === 'LIVE', 8)}
            />
            <SensorMeter
              label="Potassium"
              value={live.k}
              unit=""
              min={0}
              max={40}
              color="#7c3aed"
              hint="K · soil nutrient"
              icon={<IconBeaker />}
              {...npkState(live.k, live.status === 'LIVE', 8)}
            />
          </div>
        </Card>

        <Card title="Hourly suggestions">
          {suggestions.length === 0 ? (
            <p className="suggest-empty">
              {!greenhouseId
                ? 'Register your greenhouse to receive advice.'
                : live.status === 'LIVE'
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
      </div>
    </div>
  );
};
