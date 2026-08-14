import React from 'react';

export const Spinner: React.FC<{ size?: number }> = ({ size = 32 }) => {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
      <div
        style={{
          width: size,
          height: size,
          border: '3px solid rgba(46, 204, 113, 0.2)',
          borderTop: '3px solid var(--accent-green)',
          borderRadius: '50%',
          animation: 'spin 0.8s linear infinite',
        }}
      />
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};
