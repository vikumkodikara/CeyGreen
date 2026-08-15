import React from 'react';

export const Footer: React.FC = () => (
  <footer className="app-footer">
    <p className="footer-copy">© 2026 CeyGreen. All rights reserved.</p>
    <nav className="footer-links">
      <a href="#about">About</a>
      <a href="#privacy">Privacy</a>
      <a href="#terms">Terms</a>
      <a href="#contact">Contact</a>
    </nav>
    <div className="footer-social" aria-label="Social">
      <span>f</span>
      <span>▶</span>
      <span>◎</span>
    </div>
  </footer>
);
