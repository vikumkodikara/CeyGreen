import React, { useEffect, useRef, useState } from 'react';

type SensorMeterProps = {
  label: string;
  value: number;
  unit: string;
  min: number;
  max: number;
  color: string;
  hint: string;
  icon: React.ReactNode;
  history?: number[];
  idealMin?: number;
  idealMax?: number;
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
      const next = shownRef.current + (target - shownRef.current) * 0.55;
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

function polar(cx: number, cy: number, r: number, deg: number) {
  const rad = ((deg - 180) * Math.PI) / 180;
  return { x: cx + r * Math.cos(rad), y: cy + r * Math.sin(rad) };
}

function arcPath(cx: number, cy: number, r: number, startDeg: number, endDeg: number) {
  const start = polar(cx, cy, r, startDeg);
  const end = polar(cx, cy, r, endDeg);
  const large = endDeg - startDeg > 180 ? 1 : 0;
  return `M ${start.x} ${start.y} A ${r} ${r} 0 ${large} 1 ${end.x} ${end.y}`;
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
  idealMin,
  idealMax,
}) => {
  const shown = useSmooth(Number.isFinite(value) ? value : 0);
  const span = max - min || 1;
  const pct = clamp(((shown - min) / span) * 100, 0, 100);
  const needleDeg = pct * 1.8;
  const idle = !Number.isFinite(value);

  const cx = 100;
  const cy = 92;
  const r = 68;
  const display = idle ? '—' : shown.toFixed(shown >= 10 ? 0 : 1);

  return (
    <article className="sensor-meter" style={{ '--meter': color } as React.CSSProperties}>
      <div className="sensor-meter-top">
        <span className="sensor-meter-ico">{icon}</span>
        <div>
          <p className="sensor-meter-label">{label}</p>
          <small>{hint}</small>
        </div>
      </div>

      <div className="sensor-gauge" role="img" aria-label={`${label} ${display} ${unit}`}>
        <svg viewBox="0 0 200 118" className="sensor-gauge-svg">
          <path d={arcPath(cx, cy, r, 0, 180)} className="sensor-gauge-track" />
          <path d={arcPath(cx, cy, r, 0, needleDeg)} className="sensor-gauge-value" />

          <g
            className="sensor-gauge-needle"
            style={{ transform: `rotate(${needleDeg}deg)`, transformOrigin: `${cx}px ${cy}px` }}
          >
            <line x1={cx} y1={cy} x2={cx - r + 16} y2={cy} />
            <circle cx={cx} cy={cy} r="7" />
            <circle cx={cx} cy={cy} r="3.2" className="sensor-gauge-hub" />
          </g>
        </svg>

        <div className="sensor-gauge-readout">
          <strong>{display}</strong>
          <span>{unit}</span>
        </div>
      </div>
    </article>
  );
};
