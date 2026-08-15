import React from 'react';
import { Link } from 'react-router-dom';
import { IconLeaf } from '../icons/Icons';

export const Logo: React.FC<{ to?: string; onLight?: boolean }> = ({ to = '/', onLight }) => {
  const inner = (
    <span className={`logo ${onLight ? 'logo-on-light' : ''}`}>
      <span className="logo-mark">
        <IconLeaf size={18} />
      </span>
      <span className="logo-word">
        Cey<i>Green</i>
      </span>
    </span>
  );
  return to ? (
    <Link to={to} className="logo-link">
      {inner}
    </Link>
  ) : (
    inner
  );
};
