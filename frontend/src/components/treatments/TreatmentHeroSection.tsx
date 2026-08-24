import React from 'react';
import { IconBotanyLeaf, IconShieldCheck, IconBrainSparkle } from '../icons/DiagnosisIcons';

export const TreatmentHeroSection: React.FC = () => {
  return (
    <section className="hero-container treatment-hero-container">
      {/* Left Content */}
      <div className="hero-left-content">
        <div className="hero-badge">
          <IconBotanyLeaf size={14} />
          <span>AI PLANT HEALTH &amp; TREATMENT CATALOG</span>
        </div>

        <h1 className="hero-title">
          Cure &amp; Protect Crops<br />
          with <span className="hero-highlight">Verified Remedies</span>
        </h1>

        <p className="hero-description">
          Search organic and chemical treatment options, dosage guides, and farmer-reviewed remedies for 25+ common crop diseases across 6 major crop categories.
        </p>

        <div className="hero-pills-wrapper">
          <div className="hero-pills-row">
            <div className="feature-pill">
              <IconBotanyLeaf size={15} />
              <span>Organic &amp; Chemical Remedies</span>
            </div>
            <div className="feature-pill">
              <IconShieldCheck size={15} />
              <span>Farmer Rated &amp; Reviewed</span>
            </div>
          </div>
          <div className="hero-pills-row">
            <div className="feature-pill">
              <IconBrainSparkle size={15} />
              <span>PHI &amp; Safety Guidelines</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
