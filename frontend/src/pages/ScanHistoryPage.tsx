import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { getDiagnosisHistoryPaged } from '../api/diagnosis';
import { DiagnosisSummary, PaginatedResponse } from '../types/diagnosis';
import { ScanCard } from '../components/diagnosis/ScanCard';
import { IconSearch, IconScan, IconLeaf } from '../components/icons/Icons';
import './ScanHistoryPage.css';

const CROPS = ['All Crops', 'Tomato', 'Potato', 'Bell Pepper', 'Grape', 'Strawberry', 'Apple', 'Corn'];

export const ScanHistoryPage: React.FC = () => {
  const { user } = useAuth();
  const farmerId = user?.farmerId || user?.id || 'farmer-1';

  const [pageData, setPageData] = useState<PaginatedResponse<DiagnosisSummary> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 12;

  // Filter & Search states
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCrop, setSelectedCrop] = useState('All Crops');
  const [selectedFilter, setSelectedFilter] = useState<'all' | 'disease' | 'healthy'>('all');

  const fetchScans = useCallback(async (page: number) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getDiagnosisHistoryPaged(farmerId, page, pageSize);
      setPageData(data);
      setCurrentPage(page);
    } catch (err: any) {
      console.error('Failed to load scan history:', err);
      setError(
        err.response?.data?.message ||
          err.message ||
          'Unable to load scan history. Please check your network connection and try again.'
      );
    } finally {
      setLoading(false);
    }
  }, [farmerId, pageSize]);

  useEffect(() => {
    fetchScans(0);
  }, [fetchScans]);

  // Client-side filtering across the current page items or search
  const scans = pageData?.content || [];
  const filteredScans = scans.filter((scan) => {
    const matchesCrop =
      selectedCrop === 'All Crops' ||
      scan.cropType.toLowerCase() === selectedCrop.toLowerCase();

    const matchesSearch =
      searchQuery.trim() === '' ||
      scan.cropType.toLowerCase().includes(searchQuery.toLowerCase()) ||
      scan.predictedDisease.toLowerCase().includes(searchQuery.toLowerCase());

    const isHealthy = scan.predictedDisease.toLowerCase().includes('healthy');
    const matchesStatus =
      selectedFilter === 'all' ||
      (selectedFilter === 'healthy' && isHealthy) ||
      (selectedFilter === 'disease' && !isHealthy);

    return matchesCrop && matchesSearch && matchesStatus;
  });

  const totalPages = pageData?.totalPages || 0;
  const totalElements = pageData?.totalElements || 0;

  return (
    <div className="history-page">
      {/* Page Header */}
      <header className="history-header">
        <div className="history-header-left">
          <div className="history-badge">
            <IconScan size={15} />
            <span>Diagnosis Archive</span>
          </div>
          <h1 className="history-title">Scan History</h1>
          <p className="history-subtitle">
            Review past plant diagnoses, track crop health progression, and access treatment protocols.
          </p>
        </div>

        <div className="history-header-actions">
          <Link to="/diagnosis" className="history-cta-btn">
            <IconScan size={18} />
            <span>New Plant Scan</span>
          </Link>
        </div>
      </header>

      {/* Filter & Search Bar */}
      <section className="history-controls-card">
        <div className="history-search-wrap">
          <IconSearch size={18} className="history-search-icon" />
          <input
            type="text"
            placeholder="Search by crop or disease name..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="history-search-input"
          />
          {searchQuery && (
            <button
              type="button"
              className="history-search-clear"
              onClick={() => setSearchQuery('')}
            >
              ✕
            </button>
          )}
        </div>

        <div className="history-filters-row">
          <div className="history-crop-filter">
            <select
              value={selectedCrop}
              onChange={(e) => setSelectedCrop(e.target.value)}
              className="history-select"
            >
              {CROPS.map((crop) => (
                <option key={crop} value={crop}>
                  {crop}
                </option>
              ))}
            </select>
          </div>

          <div className="history-status-pills">
            <button
              type="button"
              className={`filter-pill-btn ${selectedFilter === 'all' ? 'active' : ''}`}
              onClick={() => setSelectedFilter('all')}
            >
              All ({scans.length})
            </button>
            <button
              type="button"
              className={`filter-pill-btn ${selectedFilter === 'disease' ? 'active' : ''}`}
              onClick={() => setSelectedFilter('disease')}
            >
              Diseases
            </button>
            <button
              type="button"
              className={`filter-pill-btn ${selectedFilter === 'healthy' ? 'active' : ''}`}
              onClick={() => setSelectedFilter('healthy')}
            >
              Healthy
            </button>
          </div>
        </div>
      </section>

      {/* Main Content Area */}
      <main className="history-content">
        {/* Loading State */}
        {loading && (
          <div className="history-grid skeleton-grid">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="scan-card-skeleton">
                <div className="skeleton-media" />
                <div className="skeleton-body">
                  <div className="skeleton-line w-75" />
                  <div className="skeleton-line w-50" />
                  <div className="skeleton-bar" />
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Error State */}
        {!loading && error && (
          <div className="history-state-card error-card">
            <div className="state-icon-wrap error-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
            </div>
            <h3>Unable to Load Scan History</h3>
            <p>{error}</p>
            <button
              type="button"
              className="retry-btn"
              onClick={() => fetchScans(currentPage)}
            >
              Retry
            </button>
          </div>
        )}

        {/* Empty State (No scans at all) */}
        {!loading && !error && scans.length === 0 && (
          <div className="history-state-card empty-card">
            <div className="state-icon-wrap empty-icon">
              <IconLeaf size={38} />
            </div>
            <h3>No Plant Scans Yet</h3>
            <p>
              Your completed plant health analyses and disease diagnoses will appear here. Start your first scan to protect your harvest.
            </p>
            <Link to="/diagnosis" className="start-scan-btn">
              <IconScan size={18} />
              <span>Start Your First Scan</span>
            </Link>
          </div>
        )}

        {/* Filtered Empty State (Scans exist but filter matches 0) */}
        {!loading && !error && scans.length > 0 && filteredScans.length === 0 && (
          <div className="history-state-card empty-filter-card">
            <h3>No Matches Found</h3>
            <p>No diagnosis records match your current search or crop filter.</p>
            <button
              type="button"
              className="clear-filters-btn"
              onClick={() => {
                setSearchQuery('');
                setSelectedCrop('All Crops');
                setSelectedFilter('all');
              }}
            >
              Reset Filters
            </button>
          </div>
        )}

        {/* Scan Cards Grid */}
        {!loading && !error && filteredScans.length > 0 && (
          <>
            <div className="history-meta-bar">
              <span className="results-count">
                Showing <strong>{filteredScans.length}</strong> of{' '}
                <strong>{totalElements > 0 ? totalElements : filteredScans.length}</strong> total scans
              </span>
            </div>

            <div className="history-grid">
              {filteredScans.map((scan) => (
                <ScanCard key={scan.diagnosisId} scan={scan} />
              ))}
            </div>

            {/* Pagination Controls */}
            {totalPages > 1 && (
              <nav className="history-pagination" aria-label="Pagination">
                <button
                  type="button"
                  disabled={currentPage === 0}
                  onClick={() => fetchScans(currentPage - 1)}
                  className="page-nav-btn"
                >
                  ← Previous
                </button>

                <div className="page-numbers">
                  {Array.from({ length: totalPages }).map((_, idx) => (
                    <button
                      key={idx}
                      type="button"
                      className={`page-num-btn ${currentPage === idx ? 'active' : ''}`}
                      onClick={() => fetchScans(idx)}
                    >
                      {idx + 1}
                    </button>
                  ))}
                </div>

                <button
                  type="button"
                  disabled={currentPage >= totalPages - 1}
                  onClick={() => fetchScans(currentPage + 1)}
                  className="page-nav-btn"
                >
                  Next →
                </button>
              </nav>
            )}
          </>
        )}
      </main>
    </div>
  );
};
