import React from 'react';
import { Link } from 'react-router-dom';

const LeafMark: React.FC<{ size?: number }> = ({ size = 20 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden fill="none">
    <path
      d="M6.2 18.6C14.1 17.4 19.4 11.2 20.4 3.6 12.4 4.7 6.8 10.4 6.2 18.6Z"
      stroke="currentColor"
      strokeWidth="1.9"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M8.6 16.2c2.4-3.1 5.6-5.2 9.6-6.6"
      stroke="currentColor"
      strokeWidth="1.9"
      strokeLinecap="round"
    />
  </svg>
);

export const Logo: React.FC<{ to?: string; onLight?: boolean }> = ({ to = '/', onLight }) => {
  const inner = (
    <span className={`logo ${onLight ? 'logo-on-light' : 'logo-on-dark'}`}>
      <span className="logo-mark" aria-hidden>
        <LeafMark />
      </span>
      <span className="logo-word">
        Cey<span>Green</span>
      </span>
    </span>
  );
  return to ? (
    <Link to={to} className="logo-link" aria-label="CeyGreen home">
      {inner}
    </Link>
  ) : (
    inner
  );
};
