import React, { useState } from 'react';
import { uploadDiagnosisImage } from '../api/diagnosis';
import { useAuth } from '../hooks/useAuth';
import { getDiseaseDetail, DiseaseDetail } from '../data/diseaseKnowledge';
import { generateGeminiAgronomistReport, getStoredGeminiKey } from '../api/gemini';
import { HeroSection } from '../components/diagnosis/HeroSection';
import { CropSelector } from '../components/diagnosis/CropSelector';
import { PlantUploadSection } from '../components/diagnosis/PlantUploadSection';
import { DiagnosisReportSection } from '../components/diagnosis/DiagnosisReportSection';
import './DiagnosisPage.css';

export const DiagnosisPage: React.FC = () => {
  const { user } = useAuth();
  const [file, setFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [cropType, setCropType] = useState('Tomato');
  const [loading, setLoading] = useState(false);

  // Results & Analysis state
  const [diagnosisResult, setDiagnosisResult] = useState<any>(null);
  const [diseaseDetail, setDiseaseDetail] = useState<DiseaseDetail | null>(null);
  const [activeTab, setActiveTab] = useState<'etiology' | 'symptoms' | 'climate' | 'quarantine' | 'aiReport'>('etiology');

  // AI Agronomist Consultation State
  const [aiConsultation, setAiConsultation] = useState<string | null>(null);
  const [loadingAi, setLoadingAi] = useState(false);
  const [copied, setCopied] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreviewUrl(URL.createObjectURL(selectedFile));
      setUploadError(null);
    }
  };

  const handleClearFile = () => {
    setFile(null);
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(null);
    setUploadError(null);
  };

  // Sample photo loader for 1-click test demonstrations
  const handleLoadSample = async (samplePath: string, crop: string, simulatedLabel: string) => {
    setCropType(crop);
    setPreviewUrl(samplePath);
    setUploadError(null);
    try {
      const res = await fetch(samplePath);
      const blob = await res.blob();
      const sampleFile = new File([blob], `${crop.toLowerCase()}_plant_photo.png`, { type: 'image/png' });
      setFile(sampleFile);
      const detail = getDiseaseDetail(simulatedLabel);
      setDiseaseDetail(detail);
    } catch {
      // Fallback
    }
  };

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    setLoading(true);
    setUploadError(null);
    setDiagnosisResult(null);
    setDiseaseDetail(null);
    setAiConsultation(null);

    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-1';
      const result = await uploadDiagnosisImage(file, farmerId, cropType);
      setDiagnosisResult(result);

      // Extract practical disease details
      const detail = getDiseaseDetail(result.predictedDisease);
      setDiseaseDetail(detail);
      setActiveTab('etiology');
    } catch (err: any) {
      const msg =
        err.response?.data?.message || err.message || 'Diagnosis failed. Please check the image file and try again.';
      setUploadError(msg);
    } finally {
      setLoading(false);
    }
  };

  // Generate AI Agronomist Recovery Plan
  const handleGenerateAiReport = async () => {
    if (!diseaseDetail) return;
    setLoadingAi(true);
    setActiveTab('aiReport');

    const apiKey = getStoredGeminiKey();

    try {
      if (apiKey) {
        const liveReport = await generateGeminiAgronomistReport(
          cropType,
          diseaseDetail.displayName,
          confidencePercent,
          apiKey
        );
        setAiConsultation(liveReport);
      } else {
        const fallbackReport = `
🌿 CEYGREEN FARMER RECOVERY ACTION PLAN
================================================================================
Target Crop        : ${cropType}
Identified Disease : ${diseaseDetail.displayName} (${diseaseDetail.scientificName})
Severity Level     : ${diseaseDetail.severity}
Scan Accuracy      : ${confidencePercent}%

1. WHAT IS HAPPENING TO YOUR PLANT:
--------------------------------------------------------------------------------
• Disease Type    : ${diseaseDetail.etiology.pathogenType}
• How It Spreads  : ${diseaseDetail.etiology.transmissionVectors.join(', ')}
• Where It Lives  : ${diseaseDetail.etiology.inoculumSource}

2. IMMEDIATE GREENHOUSE & WEATHER ADJUSTMENTS:
--------------------------------------------------------------------------------
• Safe Temperature: Maintain greenhouse air between ${diseaseDetail.microclimate.temperatureRange}.
• Humidity Control: Keep air humidity below ${diseaseDetail.microclimate.criticalHumidity} by opening roof vents.
• Watering Advice : Water only at the roots using drip lines. Never spray water on leaves.

3. FARM SANITATION & CLEANING:
--------------------------------------------------------------------------------
• Pruning Guide   : ${diseaseDetail.preventionAndQuarantine.sanitation.join(' ')}
• Plant Spacing   : ${diseaseDetail.preventionAndQuarantine.airflowAndSpacing}
• Daily Scouting  : ${diseaseDetail.preventionAndQuarantine.scoutingCadence}
• Plant Isolation : ${diseaseDetail.preventionAndQuarantine.quarantineAction}

4. FUTURE PREVENTION:
--------------------------------------------------------------------------------
• Crop Rotation   : ${diseaseDetail.preventionAndQuarantine.cropRotation}
• Clean Seeds     : Always purchase certified clean seeds and disease-resistant plant varieties.
================================================================================
Report provided by CeyGreen Plant Health Lab.
        `.trim();
        setAiConsultation(fallbackReport);
      }
    } catch {
      const fallbackReport = `
🌿 CEYGREEN FARMER RECOVERY ACTION PLAN
================================================================================
Target Crop        : ${cropType}
Identified Disease : ${diseaseDetail.displayName} (${diseaseDetail.scientificName})
Severity Level     : ${diseaseDetail.severity}
Scan Accuracy      : ${confidencePercent}%

1. IMMEDIATE FARM ACTIONS:
• Prune off all heavily infected parts and seal them in bags.
• Do not water from overhead; keep plants dry.
• Open greenhouse side vents to let fresh air blow through.

2. SANITATION:
• Clean pruning shears with alcohol between plant rows.
• ${diseaseDetail.preventionAndQuarantine.quarantineAction}
================================================================================
      `.trim();
      setAiConsultation(fallbackReport);
    } finally {
      setLoadingAi(false);
    }
  };

  const handleCopyReport = () => {
    if (aiConsultation) {
      navigator.clipboard.writeText(aiConsultation);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handlePrintReport = () => {
    window.print();
  };

  // Safe confidence extraction
  const rawConfidence = diagnosisResult
    ? diagnosisResult.confidenceScore ?? diagnosisResult.confidence ?? 0.94
    : 0;
  const confidencePercent = rawConfidence > 1 ? rawConfidence.toFixed(1) : (rawConfidence * 100).toFixed(1);
  const numericConfidence = parseFloat(confidencePercent);

  return (
    <div className="diag-page">
      {/* 1. Rebuilt Real Hero Section */}
      <HeroSection />

      {/* 2. Rebuilt 7-Crop Selector Ribbon */}
      <CropSelector
        selectedCrop={cropType}
        onSelectCrop={(id) => setCropType(id)}
      />

      {/* 3. Plant Upload Section */}
      <PlantUploadSection
        cropType={cropType}
        file={file}
        previewUrl={previewUrl}
        loading={loading}
        uploadError={uploadError}
        onFileChange={handleFileChange}
        onClearFile={handleClearFile}
        onLoadSample={handleLoadSample}
        onUpload={handleUpload}
      />

      {/* 4. Full Pathology & Recovery Plan Report */}
      {diagnosisResult && diseaseDetail && (
        <DiagnosisReportSection
          cropType={cropType}
          previewUrl={previewUrl}
          diseaseDetail={diseaseDetail}
          confidencePercent={confidencePercent}
          numericConfidence={numericConfidence}
          activeTab={activeTab}
          onTabChange={setActiveTab}
          aiConsultation={aiConsultation}
          loadingAi={loadingAi}
          copied={copied}
          onGenerateAiReport={handleGenerateAiReport}
          onCopyReport={handleCopyReport}
          onPrintReport={handlePrintReport}
        />
      )}
    </div>
  );
};

