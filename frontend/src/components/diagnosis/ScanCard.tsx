import React from 'react';
import { Link } from 'react-router-dom';
import { DiagnosisSummary } from '../../types/diagnosis';
import { resolveDiagnosisImageUrl } from '../../api/diagnosis';
import { getDiseaseDetail } from '../../data/diseaseKnowledge';

interface ScanCardProps {
  scan: DiagnosisSummary;
}

export const ScanCard: React.FC<ScanCardProps> = ({ scan }) => {
  const detail = getDiseaseDetail(scan.predictedDisease);
  const isHealthy = scan.predictedDisease?.toLowerCase().includes('healthy');
  
  // Format relative or date time
  const formatTime = (isoString?: string) => {
    if (!isoString) return 'Recent';
    try {
      const date = new Date(isoString);
      const now = new Date();
      const diffMs = now.getTime() - date.getTime();
      const diffMins = Math.floor(diffMs / (1000 * 60));
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

      if (diffMins < 1) return 'Just now';
      if (diffMins < 60) return `${diffMins}m ago`;
      if (diffHours < 24) return `${diffHours}h ago`;
      if (diffDays === 1) return 'Yesterday';
      if (diffDays < 7) return `${diffDays}d ago`;
      return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
    } catch {
      return 'Recent';
    }
  };

  const confidenceValue = scan.confidenceScore > 1 ? scan.confidenceScore : scan.confidenceScore * 100;
  const confidenceFormatted = confidenceValue.toFixed(1);

  // Badge class
  const getBadgeClass = () => {
    if (isHealthy) return 'pill-badge healthy';
    const sev = detail?.severity?.toLowerCase() || '';
    if (sev.includes('critical') || sev.includes('high')) return 'pill-badge high';
    if (sev.includes('moderate')) return 'pill-badge moderate';
    return 'pill-badge high';
  };

  const getBadgeText = () => {
    if (isHealthy) return 'Healthy';
    return detail?.severity || 'Diagnosed';
  };

  const imageUrl = resolveDiagnosisImageUrl(scan.imageUrl);

  return (
    <article className="scan-card">
      <div className="scan-card-media">
        <img
          src={imageUrl}
          alt={`${scan.cropType} diagnosis`}
          className="scan-card-img"
          onError={(e) => {
            (e.target as HTMLImageElement).src = '/dashboard/greenhouse.jpg';
          }}
          loading="lazy"
        />
        <div className="scan-card-overlay">
          <span className={getBadgeClass()}>{getBadgeText()}</span>
          <span className="scan-card-crop-tag">{scan.cropType}</span>
        </div>
      </div>

      <div className="scan-card-body">
        <div className="scan-card-header">
          <h4 className="scan-card-disease" title={detail ? detail.displayName : scan.predictedDisease}>
            {detail ? detail.displayName : scan.predictedDisease}
          </h4>
          <span className="scan-card-time">{formatTime(scan.timestamp)}</span>
        </div>

        <div className="scan-card-confidence-wrap">
          <div className="scan-card-conf-label">
            <span>Confidence</span>
            <strong className="scan-card-conf-val">{confidenceFormatted}%</strong>
          </div>
          <div className="scan-card-progress-track">
            <div
              className={`scan-card-progress-fill ${isHealthy ? 'healthy-fill' : 'disease-fill'}`}
              style={{ width: `${Math.min(100, Math.max(10, confidenceValue))}%` }}
            />
          </div>
        </div>

        <div className="scan-card-actions">
          <Link
            to={`/recent-scans/${scan.diagnosisId}`}
            className="scan-card-btn"
          >
            <span>View Diagnosis</span>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </Link>
        </div>
      </div>
    </article>
  );
};
