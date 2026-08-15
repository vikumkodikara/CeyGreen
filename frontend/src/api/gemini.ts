import axios from 'axios';

// Load Gemini API Key from environment or localStorage
export const getStoredGeminiKey = (): string => {
  return import.meta.env.VITE_GEMINI_API_KEY || localStorage.getItem('ceygreen_gemini_api_key') || '';
};

export const setStoredGeminiKey = (key: string): void => {
  if (key) {
    localStorage.setItem('ceygreen_gemini_api_key', key.trim());
  } else {
    localStorage.removeItem('ceygreen_gemini_api_key');
  }
};

export const generateGeminiAgronomistReport = async (
  cropType: string,
  diseaseName: string,
  confidencePercent: string,
  customKey?: string
): Promise<string> => {
  const apiKey = (customKey || getStoredGeminiKey()).trim();

  if (!apiKey) {
    throw new Error('Gemini API Key is missing. Please add VITE_GEMINI_API_KEY in client/.env or enter your key in the AI setting box.');
  }

  const prompt = `You are CeyGreen's Senior AI Agronomist Specialist.
A farmer has submitted a crop leaf photograph for AI diagnosis.
• Target Crop: ${cropType}
• Diagnosed Condition: ${diseaseName}
• AI Confidence Score: ${confidencePercent}%

Please provide an expert, highly practical Agronomist Action Report formatted with clear bullet points:
1. 🔬 Disease Overview & Urgency Assessment
2. 🌡️ Immediate Greenhouse Climate Adjustments (Humidity, Ventilation, Pruning)
3. 💊 14-Day Step-by-Step Recovery & Spray Schedule (Organic & Chemical options with dosage)
4. 🛡️ Preventive IPM Advice for neighboring crops

Keep the advice authoritative, easy to read, and immediately actionable for a greenhouse farmer.`;

  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`;

  try {
    const response = await axios.post(url, {
      contents: [
        {
          parts: [{ text: prompt }]
        }
      ]
    });

    const candidateText = response.data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!candidateText) {
      throw new Error('Received an empty response from Gemini API.');
    }

    return candidateText;
  } catch (err: any) {
    if (err.response?.data?.error?.message) {
      throw new Error(`Gemini API Error: ${err.response.data.error.message}`);
    }
    throw new Error(err.message || 'Failed to connect to Google Gemini API.');
  }
};
