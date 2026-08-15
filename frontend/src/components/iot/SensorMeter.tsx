import React, { useEffect, useMemo, useRef, useState } from 'react';

type SensorMeterProps = {
  label: string;
  value: number;
  unit: string;
  min: number;
  max: number;
  color: string;
  hint: string;
  icon: React.ReactNode;
  history: number[];
};

function clamp(n: number, min: number, max: number) {
  return Math.min(max, Math.max(min, n));
}

function useSmooth(target: number) {
  const [shown, setShown] = useState(target);
  const shownRef = useRef(target);

  useEffect(() => {
    let frame = 0;
    const tick = () => {
      const next = shownRef.current + (target - shownRef.current) * 0.18;
      if (Math.abs(target - next) < 0.05) {
        shownRef.current = target;
        setShown(target);
        return;
      }
      shownRef.current = next;
      setShown(next);
      frame = window.requestAnimationFrame(tick);
    };
    frame = window.requestAnimationFrame(tick);
    return () => window.cancelAnimationFrame(frame);
  }, [target]);

  return shown;
}

function Spark({ values, color }: { values: number[]; color: string }) {
  const pts = useMemo(() => {
    if (values.length < 2) return '';
    const min = Math.min(...values);
    const max = Math.max(...values);
    return values
      .map((v, i) => {
        const x = (i / (values.length - 1)) * 100;
        const y = 28 - ((v - min) / (max - min || 1)) * 22;
        return `${x},${y}`;
      })
      .join(' ');
  }, [values]);

  if (!pts) return <div className="sensor-spark-empty" />;

  return (
    <svg viewBox="0 0 100 32" className="sensor-spark" preserveAspectRatio="none" aria-hidden>
      <polyline fill="none" stroke={color} strokeWidth="2.4" strokeLinejoin="round" strokeLinecap="round" points={pts} />
    </svg>
  );
}

export const SensorMeter: React.FC<SensorMeterProps> = ({
  label,
  value,
  unit,
  min,
  max,
  color,
  hint,
  icon,
  history,
}) => {
  const shown = useSmooth(Number.isFinite(value) ? value : 0);
  const pct = clamp(((shown - min) / (max - min || 1)) * 100, 0, 100);
  const radius = 46;
  const circ = 2 * Math.PI * radius;
  const dash = circ * (1 - pct / 100);
  const idle = shown <= min && pct < 1;

  return (
    <article className="sensor-meter" style={{ '--meter': color } as React.CSSProperties}>
      <div className="sensor-meter-top">
        <span className="sensor-meter-ico">{icon}</span>
        <div>
          <p className="sensor-meter-label">{label}</p>
          <small>{hint}</small>
        </div>
      </div>

      <div className="sensor-meter-body">
        <div className="sensor-ring-wrap">
          <svg viewBox="0 0 120 120" className="sensor-ring" aria-hidden>
            <circle cx="60" cy="60" r={radius} className="sensor-ring-track" />
            <circle
              cx="60"
              cy="60"
              r={radius}
              className="sensor-ring-fill"
              strokeDasharray={circ}
              strokeDashoffset={dash}
            />
          </svg>
          <div className="sensor-ring-value">
            <strong>{idle && value === 0 ? '—' : shown.toFixed(shown >= 10 ? 0 : 1)}</strong>
            <span>{unit}</span>
          </div>
        </div>
      </div>

      <div className="sensor-track" aria-hidden>
        <i style={{ width: `${pct}%` }} />
        <b style={{ left: `${pct}%` }} />
      </div>

      <Spark values={history} color={color} />
    </article>
  );
};
