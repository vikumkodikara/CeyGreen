import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  className = '',
  disabled,
  ...props
}) => {
  const baseStyle: React.CSSProperties = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '0.5rem',
    fontWeight: 600,
    borderRadius: 12,
    cursor: disabled || isLoading ? 'not-allowed' : 'pointer',
    opacity: disabled || isLoading ? 0.6 : 1,
    transition: 'all 0.2s ease',
  };

  const variantStyles: Record<string, React.CSSProperties> = {
    primary: {
      background: 'var(--accent-green)',
      color: 'var(--on-accent)',
    },
    secondary: {
      background: '#fff',
      color: 'var(--text-main)',
      border: '1px solid var(--border-color)',
    },
    danger: {
      background: 'var(--danger)',
      color: '#ffffff',
    },
    ghost: {
      background: 'transparent',
      color: 'var(--text-muted)',
    },
  };

  const sizeStyles: Record<string, React.CSSProperties> = {
    sm: { padding: '0.4rem 0.8rem', fontSize: '0.85rem' },
    md: { padding: '0.65rem 1.25rem', fontSize: '0.95rem' },
    lg: { padding: '0.85rem 1.75rem', fontSize: '1.05rem' },
  };

  return (
    <button
      style={{
        ...baseStyle,
        ...variantStyles[variant],
        ...sizeStyles[size],
      }}
      disabled={disabled || isLoading}
      className={className}
      {...props}
    >
      {isLoading ? <span>Loading...</span> : children}
    </button>
  );
};
