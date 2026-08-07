import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ label, error, className = '', ...props }) => {
  return (
    <div style={{ marginBottom: '1rem', width: '100%' }}>
      {label && (
        <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)', fontWeight: 500 }}>
          {label}
        </label>
      )}
      <input className={className} {...props} />
      {error && <span style={{ display: 'block', marginTop: '0.3rem', fontSize: '0.8rem', color: 'var(--danger)' }}>{error}</span>}
    </div>
  );
};
