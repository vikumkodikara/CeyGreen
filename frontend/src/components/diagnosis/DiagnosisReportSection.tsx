import React from 'react';
import { DiseaseDetail } from '../../data/diseaseKnowledge';
import {
  IconMicroscope,
  IconEyeScan,
  IconThermometerGauge,
  IconShieldCheck,
  IconBrainSparkle,
  IconCopyDoc,
  IconPrintExport,
  IconAlertSign,
} from '../icons/DiagnosisIcons';

interface DiagnosisReportSectionProps {
  cropType: string;
  previewUrl: string | null;
  diseaseDetail: DiseaseDetail;
  confidencePercent: string;
  numericConfidence: number;
  activeTab: 'etiology' | 'symptoms' | 'climate' | 'quarantine' | 'aiReport';
  onTabChange: (tab: 'etiology' | 'symptoms' | 'climate' | 'quarantine' | 'aiReport') => void;
  aiConsultation: string | null;
  loadingAi: boolean;
  copied: boolean;
  onGenerateAiReport: () => void;
  onCopyReport: () => void;
  onPrintReport: () => void;
}

export const DiagnosisReportSection: React.FC<DiagnosisReportSectionProps> = ({
  cropType,
  previewUrl,
  diseaseDetail,
  confidencePercent,
  numericConfidence,
  activeTab,
  onTabChange,
  aiConsultation,
  loadingAi,
  copied,
  onGenerateAiReport,
  onCopyReport,
  onPrintReport,
}) => {
  // Circular gauge calculations
  const radius = 24;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (numericConfidence / 100) * circumference;

  const getSeverityLevel = (sev: string): number => {
    switch (sev?.toLowerCase()) {
      case 'critical': return 4;
      case 'high': return 3;
      case 'moderate': return 2;
      case 'healthy': return 1;
      default: return 2;
    }
  };

  const currentLevel = getSeverityLevel(diseaseDetail.severity);

  const getPillBadgeClass = (sev: string) => {
    switch (sev?.toLowerCase()) {
      case 'critical': return 'critical';
      case 'high': return 'high';
      case 'moderate': return 'moderate';
      case 'healthy': return 'healthy';
      default: return 'moderate';
    }
  };

  return (
    <div className="report-container">
      {/* Main Result Dossier Card */}
      <div className="dossier-hero-card">
        <div className="dossier-header-bar">
          <div>
            <div className="dossier-badge-pills">
              <span className={`pill-badge ${getPillBadgeClass(diseaseDetail.severity)}`}>
                {diseaseDetail.severity} Risk
              </span>
              <span className="condition-pill">
                {diseaseDetail.category} Condition
              </span>
              <span className="crop-pill">
                Crop: {cropType}
              </span>
            </div>

            <h2 className="dossier-title">{diseaseDetail.displayName}</h2>
            <p className="dossier-latin">{diseaseDetail.scientificName}</p>
          </div>

          {/* Circular Accuracy Gauge */}
          <div className="svg-gauge-box">
            <div className="svg-gauge-visual">
              <svg width="58" height="58" viewBox="0 0 58 58">
                <circle
                  cx="29"
                  cy="29"
                  r={radius}
                  stroke="#e2ede5"
                  strokeWidth="5"
                  fill="transparent"
                />
                <circle
                  cx="29"
                  cy="29"
                  r={radius}
                  stroke={numericConfidence >= 80 ? '#22c55e' : '#f59e0b'}
                  strokeWidth="5"
                  fill="transparent"
                  strokeDasharray={circumference}
                  strokeDashoffset={strokeDashoffset}
                  strokeLinecap="round"
                  className="svg-gauge-circle"
                />
              </svg>
              <div className="svg-gauge-text">{confidencePercent}%</div>
            </div>
            <div className="svg-gauge-label">
              <small>Diagnosis Match</small>
              <strong>{numericConfidence >= 80 ? 'High Confidence' : 'Moderate Match'}</strong>
            </div>
          </div>
        </div>

        {/* 4-Stage Severity Scale */}
        <div className="hazard-scale-box">
          <div className="hazard-scale-head">
            <span>Disease Severity Level</span>
            <span>Level {currentLevel} of 4: <strong>{diseaseDetail.severity}</strong></span>
          </div>
          <div className="hazard-segments">
            <div className={`hazard-bar ${currentLevel >= 1 ? 'active level-1' : ''}`} />
            <div className={`hazard-bar ${currentLevel >= 2 ? 'active level-2' : ''}`} />
            <div className={`hazard-bar ${currentLevel >= 3 ? 'active level-3' : ''}`} />
            <div className={`hazard-bar ${currentLevel >= 4 ? 'active level-4' : ''}`} />
          </div>
        </div>

        {/* Plant Photo & Detection Summary */}
        <div className="result-leaf-box">
          {previewUrl && (
            <img src={previewUrl} alt="Analyzed plant" className="result-leaf-img" />
          )}
          <div className="result-summary-text">
            <h4>What the AI Detected</h4>
            <p>{diseaseDetail.description}</p>
          </div>
        </div>

        {/* AI Recovery Plan Button */}
        <button
          type="button"
          className="btn-gemini-report"
          onClick={onGenerateAiReport}
          disabled={loadingAi}
        >
          <IconBrainSparkle size={18} />
          {loadingAi ? 'Creating Step-by-Step Farmer Guide...' : '📋 Generate Step-by-Step Farmer Recovery Plan (AI Advice)'}
        </button>

        {/* AI Action Report Terminal View */}
        {aiConsultation && activeTab === 'aiReport' && (
          <div className="ai-terminal-box" style={{ marginTop: '1.15rem' }}>
            <div className="ai-terminal-toolbar">
              <div className="ai-terminal-title">
                <IconMicroscope size={18} /> Farmer Action &amp; Recovery Plan
              </div>
              <div className="ai-terminal-actions">
                <button type="button" className="btn-terminal-action" onClick={onCopyReport}>
                  <IconCopyDoc size={14} /> {copied ? 'Copied' : 'Copy Text'}
                </button>
                <button type="button" className="btn-terminal-action" onClick={onPrintReport}>
                  <IconPrintExport size={14} /> Print Plan
                </button>
              </div>
            </div>
            <pre className="ai-terminal-content">{aiConsultation}</pre>
          </div>
        )}
      </div>

      {/* Diagnostic Navigation Tabs */}
      <div className="dossier-tabs">
        <button
          type="button"
          className={`dossier-tab-btn ${activeTab === 'etiology' ? 'active' : ''}`}
          onClick={() => onTabChange('etiology')}
        >
          <IconMicroscope size={16} /> About This Disease
        </button>
        <button
          type="button"
          className={`dossier-tab-btn ${activeTab === 'symptoms' ? 'active' : ''}`}
          onClick={() => onTabChange('symptoms')}
        >
          <IconEyeScan size={16} /> Symptoms to Look For
        </button>
        <button
          type="button"
          className={`dossier-tab-btn ${activeTab === 'climate' ? 'active' : ''}`}
          onClick={() => onTabChange('climate')}
        >
          <IconThermometerGauge size={16} /> Weather &amp; Climate Risks
        </button>
        <button
          type="button"
          className={`dossier-tab-btn ${activeTab === 'quarantine' ? 'active' : ''}`}
          onClick={() => onTabChange('quarantine')}
        >
          <IconShieldCheck size={16} /> Prevention &amp; Farm Care
        </button>
        {aiConsultation && (
          <button
            type="button"
            className={`dossier-tab-btn ${activeTab === 'aiReport' ? 'active' : ''}`}
            onClick={() => onTabChange('aiReport')}
          >
            <IconBrainSparkle size={16} /> AI Recovery Plan
          </button>
        )}
      </div>

      {/* Tab 1: About This Disease */}
      {activeTab === 'etiology' && (
        <div className="report-card">
          <div className="diag-card-head">
            <h2><IconMicroscope size={20} /> Disease Information &amp; Causes</h2>
          </div>
          <div className="report-grid-2">
            <div className="report-box">
              <h4>Disease Type &amp; Name</h4>
              <p style={{ fontSize: '0.88rem', color: 'var(--jade)', fontWeight: 700, marginBottom: '0.35rem' }}>
                {diseaseDetail.etiology.pathogenType}
              </p>
              <p style={{ fontSize: '0.82rem', color: 'var(--ink-3)' }}>
                <strong>Scientific Name:</strong> <em>{diseaseDetail.scientificName}</em>
              </p>
              <p style={{ fontSize: '0.82rem', color: 'var(--ink-3)', marginTop: '0.25rem' }}>
                <strong>Time to Show Symptoms:</strong> {diseaseDetail.etiology.incubationPeriod}
              </p>
            </div>

            <div className="report-box">
              <h4>Where Spores Come From</h4>
              <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', lineHeight: 1.5 }}>
                {diseaseDetail.etiology.inoculumSource}
              </p>
            </div>
          </div>

          <div className="report-box" style={{ marginTop: '0.85rem' }}>
            <h4>How It Enters the Plant</h4>
            <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', lineHeight: 1.55, marginBottom: '0.6rem' }}>
              {diseaseDetail.etiology.hostInvasionMechanism}
            </p>
            <h4>How It Spreads Across Your Farm:</h4>
            <ul className="report-list">
              {diseaseDetail.etiology.transmissionVectors.map((v, i) => (
                <li key={i}>{v}</li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {/* Tab 2: Symptoms to Look For */}
      {activeTab === 'symptoms' && (
        <div className="report-card">
          <div className="diag-card-head">
            <h2><IconEyeScan size={20} /> What Symptoms to Look For on Your Plants</h2>
          </div>
          <div className="report-grid-2">
            <div className="report-box">
              <h4>Signs on Leaves &amp; Foliage</h4>
              <ul className="report-list">
                {diseaseDetail.symptoms.leafMarkers.map((m, i) => (
                  <li key={i}>{m}</li>
                ))}
              </ul>
            </div>

            <div className="report-box">
              <h4>How It Spreads on the Plant</h4>
              <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', lineHeight: 1.5, marginBottom: '0.6rem' }}>
                {diseaseDetail.symptoms.canopyProgression}
              </p>
              <h4>Signs on Stems &amp; Fruit</h4>
              <ul className="report-list">
                {diseaseDetail.symptoms.stemAndFruitSigns.map((s, i) => (
                  <li key={i}>{s}</li>
                ))}
              </ul>
            </div>
          </div>

          {/* Differential Look-Alikes Table */}
          <div className="report-box" style={{ marginTop: '0.85rem', background: '#fffbeb', borderColor: '#fef3c7' }}>
            <h4 style={{ color: '#b45309' }}>
              <IconAlertSign size={16} /> How to Distinguish from Similar Problems
            </h4>
            <table className="lookalike-table">
              <thead>
                <tr>
                  <th>Condition</th>
                  <th>Symptoms</th>
                  <th>How to Tell the Difference</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>{diseaseDetail.displayName}</strong></td>
                  <td>{diseaseDetail.symptoms.leafMarkers[0]}</td>
                  <td>Specific fungal rings or dark spots with halos</td>
                </tr>
                {diseaseDetail.symptoms.lookAlikes.map((l, i) => (
                  <tr key={i}>
                    <td>Similar Issue #{i + 1}</td>
                    <td>{l}</td>
                    <td>Caused by nutrient shortages or weather stress</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 3: Weather & Climate Risks */}
      {activeTab === 'climate' && (
        <div className="report-card">
          <div className="diag-card-head">
            <h2><IconThermometerGauge size={20} /> Weather &amp; Greenhouse Climate Triggers</h2>
          </div>
          <div className="report-grid-4">
            <div className="climate-dial-tile">
              <span>Optimal Temperature</span>
              <strong>{diseaseDetail.microclimate.temperatureRange}</strong>
              <small>Favorable for disease</small>
            </div>

            <div className="climate-dial-tile">
              <span>Dangerous Humidity</span>
              <strong style={{ color: '#b91c1c' }}>{diseaseDetail.microclimate.criticalHumidity}</strong>
              <small>High risk of rapid spread</small>
            </div>

            <div className="climate-dial-tile">
              <span>Moisture Duration</span>
              <strong>{diseaseDetail.microclimate.leafWetnessHours}</strong>
              <small>Standing water duration</small>
            </div>

            <div className="climate-dial-tile">
              <span>Air Circulation Risk</span>
              <strong style={{ color: diseaseDetail.microclimate.vpdRiskLevel === 'Extreme' ? '#b91c1c' : '#b45309' }}>
                {diseaseDetail.microclimate.vpdRiskLevel} Risk
              </strong>
              <small>Greenhouse air index</small>
            </div>
          </div>

          <div className="report-box" style={{ marginTop: '0.85rem' }}>
            <h4>How to Control Greenhouse Weather to Stop Spread</h4>
            <ul className="report-list">
              <li>Turn on roof ventilation fans in the morning to clear fog and condensation.</li>
              <li>Prune lower foliage so fresh air and sunlight reach the bottom of the plant.</li>
              <li>Avoid overhead spraying; keep plant surfaces completely dry during humid days.</li>
            </ul>
          </div>
        </div>
      )}

      {/* Tab 4: Prevention & Farm Care */}
      {activeTab === 'quarantine' && (
        <div className="report-card">
          <div className="diag-card-head">
            <h2><IconShieldCheck size={20} /> Long-Term Prevention &amp; Farm Protection</h2>
          </div>
          <div className="report-grid-2">
            <div className="report-box">
              <h4>Cleaning &amp; Pruning Habits</h4>
              <ul className="report-list">
                {diseaseDetail.preventionAndQuarantine.sanitation.map((s, i) => (
                  <li key={i}>{s}</li>
                ))}
              </ul>
            </div>

            <div className="report-box">
              <h4>Crop Rotation &amp; Spacing</h4>
              <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', marginBottom: '0.45rem', lineHeight: 1.45 }}>
                <strong>Crop Rotation:</strong> {diseaseDetail.preventionAndQuarantine.cropRotation}
              </p>
              <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', marginBottom: '0.45rem', lineHeight: 1.45 }}>
                <strong>Plant Spacing:</strong> {diseaseDetail.preventionAndQuarantine.airflowAndSpacing}
              </p>
              <p style={{ fontSize: '0.84rem', color: 'var(--ink-2)', lineHeight: 1.45 }}>
                <strong>Scouting Routine:</strong> {diseaseDetail.preventionAndQuarantine.scoutingCadence}
              </p>
            </div>
          </div>

          <div className="report-box" style={{ marginTop: '0.85rem', background: '#fef2f2', borderColor: '#fee2e2' }}>
            <h4 style={{ color: '#b91c1c' }}>
              <IconAlertSign size={16} /> ⚠️ Urgent Action to Protect Nearby Healthy Plants
            </h4>
            <p style={{ fontSize: '0.86rem', color: '#991b1b', lineHeight: 1.5 }}>
              {diseaseDetail.preventionAndQuarantine.quarantineAction}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};
