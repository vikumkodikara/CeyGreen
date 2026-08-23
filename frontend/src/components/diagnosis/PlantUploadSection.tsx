import React from 'react';
import {
  IconCameraScan,
  IconBotanyLeaf,
  IconAlertSign,
} from '../icons/DiagnosisIcons';
import { IconScan } from '../icons/Icons';

interface PlantUploadSectionProps {
  cropType: string;
  file: File | null;
  previewUrl: string | null;
  loading: boolean;
  uploadError: string | null;
  onFileChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onClearFile: () => void;
  onLoadSample: (samplePath: string, crop: string, label: string) => void;
  onUpload: (e: React.FormEvent) => void;
}

export const PlantUploadSection: React.FC<PlantUploadSectionProps> = ({
  cropType,
  file,
  previewUrl,
  loading,
  uploadError,
  onFileChange,
  onClearFile,
  onLoadSample,
  onUpload,
}) => {
  return (
    <div className="upload-card">
      <div className="upload-card-head">
        <div>
          <h2>
            <IconCameraScan size={22} /> Upload a Plant Photo
          </h2>
          <p>
            Select your crop, attach a clear photo of the affected plant, and our AI will help you identify the disease.
          </p>
        </div>
        <span className="live-status-pill">
          <i /> AI Ready to Diagnose
        </span>
      </div>

      {uploadError && (
        <div className="alert alert-error" style={{ marginBottom: '1rem' }}>
          <IconAlertSign size={16} /> {uploadError}
        </div>
      )}

      <form onSubmit={onUpload}>
        {/* Sample Photos Bar */}
        <div className="sample-photos-bar">
          <span className="sample-label">Try with Sample Photos:</span>
          <button
            type="button"
            className="sample-btn"
            onClick={() =>
              onLoadSample(
                '/dashboard/blight.png',
                'Tomato',
                'Tomato___Early_blight'
              )
            }
          >
            <IconBotanyLeaf size={14} /> Tomato Early Blight Sample
          </button>
          <button
            type="button"
            className="sample-btn"
            onClick={() =>
              onLoadSample('/dashboard/greenhouse.jpg', 'Tomato', 'Healthy')
            }
          >
            <IconBotanyLeaf size={14} /> Healthy Tomato Crop Sample
          </button>
        </div>

        {/* Dropzone or Preview */}
        {!previewUrl ? (
          <label
            className="upload-dropzone"
            onDragOver={(e) => e.preventDefault()}
            onDrop={(e) => {
              e.preventDefault();
              const dropped = e.dataTransfer.files?.[0];
              if (dropped) {
                const event = {
                  target: { files: [dropped] },
                } as unknown as React.ChangeEvent<HTMLInputElement>;
                onFileChange(event);
              }
            }}
          >
            <div className="dropzone-icon-box">
              <IconCameraScan size={28} />
            </div>
            <strong className="dropzone-title">
              Click to choose a photo or drag your plant image here
            </strong>
            <span className="dropzone-subtitle">
              Supports phone photos, JPG, PNG, and WebP (up to 10 MB)
            </span>
            <input
              type="file"
              accept="image/png, image/jpeg, image/webp"
              onChange={onFileChange}
              style={{ display: 'none' }}
            />
          </label>
        ) : (
          <div className="upload-preview-frame">
            <div className="preview-image-box">
              <img src={previewUrl} alt="Plant preview" />
              <div className="laser-sweep-line" />
            </div>
            <div className="preview-meta-info">
              <div className="preview-meta-title">
                {file?.name || `${cropType} Plant Photo`}
              </div>
              <div className="preview-meta-desc">
                Crop: <strong>{cropType}</strong> •{' '}
                {file ? (file.size / (1024 * 1024)).toFixed(2) + ' MB' : 'Photo Sample'}{' '}
                • Ready for diagnosis
              </div>
            </div>
            <button
              type="button"
              className="btn-remove-photo"
              onClick={onClearFile}
            >
              Remove Photo
            </button>
          </div>
        )}

        <button
          type="submit"
          className="btn-diagnose-action"
          disabled={!file || loading}
        >
          {loading ? (
            <>
              <IconScan size={20} /> Analyzing Plant Health...
            </>
          ) : (
            <>
              <IconScan size={20} /> 🌿 Diagnose Plant Disease Now
            </>
          )}
        </button>
      </form>
    </div>
  );
};
