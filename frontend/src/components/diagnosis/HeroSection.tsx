import React from 'react';
import { IconBotanyLeaf, IconShieldCheck, IconBrainSparkle } from '../icons/DiagnosisIcons';

export const HeroSection: React.FC = () => {
  return (
    <section className="hero-container">
      {/* Left Content */}
      <div className="hero-left-content">
        <div className="hero-badge">
          <IconBotanyLeaf size={14} />
          <span>AI PLANT HEALTH &amp; DISEASE SCANNER</span>
        </div>

        <h1 className="hero-title">
          Diagnose Plant Diseases<br />
          in <span className="hero-highlight">Seconds</span>
        </h1>

        <p className="hero-description">
          Take or upload a photo of any sick plant, leaf, stem, or fruit to instantly identify diseases early, check greenhouse weather risks, and get practical recovery advice.
        </p>

        <div className="hero-pills-wrapper">
          <div className="hero-pills-row">
            <div className="feature-pill">
              <IconBotanyLeaf size={15} />
              <span>6 Crop Categories</span>
            </div>
            <div className="feature-pill">
              <IconShieldCheck size={15} />
              <span>25+ Common Diseases Detected</span>
            </div>
          </div>
          <div className="hero-pills-row">
            <div className="feature-pill">
              <IconBrainSparkle size={15} />
              <span>Instant Farmer Advice</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right Visual Image */}
      <div className="hero-right-visual">
        <img
          src="/images/hero/plant-disease-scanner.jpg"
          alt="AI Plant Disease Diagnosis Scanner in Greenhouse"
          className="hero-scanner-img"
        />
      </div>
    </section>
  );
};
