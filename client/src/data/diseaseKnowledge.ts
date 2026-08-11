export interface DiseaseDetail {
  name: string;
  displayName: string;
  crop: string;
  category: 'Fungal' | 'Bacterial' | 'Viral' | 'Pest' | 'Healthy';
  severity: 'Critical' | 'High' | 'Moderate' | 'Low' | 'Healthy';
  description: string;
  symptoms: string[];
  causes: string[];
  organicTreatments: string[];
  chemicalTreatments: string[];
  prevention: string[];
  recommendedProducts: Array<{ name: string; type: string; dosage: string }>;
}

export const CROPS_LIST = [
  { id: 'Tomato', label: 'Tomato', icon: '🍅', description: 'Solanum lycopersicum' },
  { id: 'Potato', label: 'Potato', icon: '🥔', description: 'Solanum tuberosum' },
  { id: 'Pepper', label: 'Pepper / Bell Pepper', icon: '🫑', description: 'Capsicum annuum' },
  { id: 'Strawberry', label: 'Strawberry', icon: '🍓', description: 'Fragaria × ananassa' },
  { id: 'Grape', label: 'Grape', icon: '🍇', description: 'Vitis vinifera' },
  { id: 'Other', label: 'Other Greenhouse Crop', icon: '🌱', description: 'General Crop Diagnostics' },
];

export const DISEASE_KNOWLEDGE: Record<string, DiseaseDetail> = {
  // Pepper Bacterial Spot
  'Pepper,_bell___Bacterial_spot': {
    name: 'Pepper,_bell___Bacterial_spot',
    displayName: 'Pepper Bacterial Spot (Xanthomonas euvesicatoria)',
    crop: 'Pepper / Bell Pepper',
    category: 'Bacterial',
    severity: 'High',
    description: 'Bacterial spot is one of the most destructive diseases affecting bell peppers. It causes dark water-soaked spots on leaves and fruit, leading to premature defoliation and severe yield loss in humid greenhouse conditions.',
    symptoms: [
      'Small, dark brown to black water-soaked spots on lower leaves',
      'Leaves turning yellow around lesions and dropping prematurely',
      'Raised, rough scab-like spots on immature green pepper fruit',
    ],
    causes: [
      'Infected seeds or plant debris',
      'High humidity (>85%) and temperatures between 24°C–30°C',
      'Overhead irrigation splashing bacteria between plants',
    ],
    organicTreatments: [
      'Apply Copper Octanoate (Soap-based copper spray) every 7 days',
      'Spray Bacillus subtilis microbial bactericide to suppress bacterial growth',
      'Remove and safely destroy severely infected leaves',
    ],
    chemicalTreatments: [
      'Copper Hydroxide + Mancozeb tank mix sprayed at first sign of infection',
      'Streptomycin Sulfate spray (where permitted for agricultural use)',
    ],
    prevention: [
      'Use certified pathogen-free pepper seeds',
      'Switch to drip irrigation to prevent water splashing',
      'Ensure 3-year crop rotation with non-solanaceous crops',
    ],
    recommendedProducts: [
      { name: 'CeyBio CopperShield Liquid', type: 'Organic Bactericide', dosage: '2.5 ml / Litre of water' },
      { name: 'MicrobMax Bacillus Guard', type: 'Biological Agent', dosage: '3 g / Litre of water' },
    ],
  },

  // Tomato Early Blight
  'Tomato___Early_blight': {
    name: 'Tomato___Early_blight',
    displayName: 'Tomato Early Blight (Alternaria solani)',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Early blight is a common fungal disease of tomatoes characterized by target-like concentric rings on older lower leaves. Left untreated, it causes rapid leaf collapse and sunscald on fruit.',
    symptoms: [
      'Concentric target-like rings within dark brown spots on mature leaves',
      'Yellow halo surrounding leaf lesions',
      'Sunken, dark leathery lesions at the stem end of fruits',
    ],
    causes: [
      'Spores carried by wind, rain, and tools',
      'Favorable in warm weather (24°C–29°C) with prolonged wet periods',
    ],
    organicTreatments: [
      'Neem oil 70% EC emulsion sprayed every 5–7 days',
      'Potassium Bicarbonate spray to raise foliar pH and inhibit fungal spore germination',
    ],
    chemicalTreatments: [
      'Chlorothalonil 75% WP protective spray',
      'Azoxystrobin + Difenoconazole broad-spectrum systemic fungicide',
    ],
    prevention: [
      'Prune lower leaves up to 30 cm above soil level to minimize soil splash',
      'Mulch soil surface with straw or weed fabric',
    ],
    recommendedProducts: [
      { name: 'CeyFungi Chlorothalonil 75', type: 'Fungicide', dosage: '2 g / Litre of water' },
      { name: 'BioNeem Pure EC', type: 'Organic Spray', dosage: '5 ml / Litre of water' },
    ],
  },

  // Tomato Late Blight
  'Tomato___Late_blight': {
    name: 'Tomato___Late_blight',
    displayName: 'Tomato Late Blight (Phytophthora infestans)',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'Late blight is an aggressive water mold capable of destroying entire greenhouse tomato crops within days. Symptoms include large grey-green greasy spots on leaves with white cottony mildew growth on undersides.',
    symptoms: [
      'Large, irregular dark grey or pale green water-soaked spots on foliage',
      'White fuzzy fungal growth on the underside of leaves during humid weather',
      'Firm, brown greasy rot extending deep into green fruit tissue',
    ],
    causes: [
      'Cool temperatures (15°C–22°C) combined with high relative humidity (>90%)',
      'Wind-borne sporangia travelling from infected neighboring fields',
    ],
    organicTreatments: [
      'Copper Hydroxide 50% WP preventative spray',
      'Trichoderma harzianum soil and foliar bio-fungicide',
    ],
    chemicalTreatments: [
      'Metalaxyl + Mancozeb systemic formulation for curative action',
      'Dimethomorph or Cymoxanil spray applied immediately at outbreak',
    ],
    prevention: [
      'Maintain greenhouse humidity below 80% using exhaust fans',
      'Destroy all infected crop residues immediately upon detection',
    ],
    recommendedProducts: [
      { name: 'CeyCure Metalaxyl-M Systemic', type: 'Fungicide', dosage: '1.5 g / Litre of water' },
      { name: 'CopperCure 50 WP', type: 'Protectant', dosage: '3 g / Litre of water' },
    ],
  },

  // Tomato Bacterial Spot
  'Tomato___Bacterial_spot': {
    name: 'Tomato___Bacterial_spot',
    displayName: 'Tomato Bacterial Spot (Xanthomonas perforans)',
    crop: 'Tomato',
    category: 'Bacterial',
    severity: 'High',
    description: 'Bacterial spot produces numerous small black specks on tomato leaves and stem tissue. Lesions dry out, causing leaves to appear shot through with small holes.',
    symptoms: [
      'Numerous small (1-3mm) dark brown or black angular spots on leaves',
      'Yellowing of foliage surrounding heavy spot clusters',
      'Blister-like spots on green fruit that turn scabbed and sunken',
    ],
    causes: ['Infected seeds, warm wet weather, and mechanical transmission during pruning'],
    organicTreatments: ['Copper Soap spray mixed with Neem oil extract'],
    chemicalTreatments: ['Copper Hydroxide combined with Mancozeb'],
    prevention: ['Use disease-resistant varieties and avoid working in wet fields'],
    recommendedProducts: [
      { name: 'CeyBio CopperShield Liquid', type: 'Organic Bactericide', dosage: '2.5 ml / Litre' },
    ],
  },

  // Potato Late Blight
  'Potato___Late_blight': {
    name: 'Potato___Late_blight',
    displayName: 'Potato Late Blight (Phytophthora infestans)',
    crop: 'Potato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'Potato late blight causes rapid foliage decay and tuber rot. It can wipe out potato canopy within 7 to 10 days if unchecked under humid conditions.',
    symptoms: [
      'Dark water-soaked lesions on leaf tips and margins',
      'White downy growth on the undersides of leaves in wet morning conditions',
      'Reddish-brown dry rot extending into potato tubers',
    ],
    causes: ['Infected seed tubers, cool wet weather (13°C–21°C)'],
    organicTreatments: ['Copper Sulfate / Bordeaux Mixture spray'],
    chemicalTreatments: ['Mancozeb 85% WP, Propamocarb Hydrochloride'],
    prevention: ['Plant certified seed tubers and mound soil well around base of plants'],
    recommendedProducts: [
      { name: 'CeyCure Metalaxyl-M Systemic', type: 'Fungicide', dosage: '1.5 g / Litre' },
    ],
  },

  // Potato Early Blight
  'Potato___Early_blight': {
    name: 'Potato___Early_blight',
    displayName: 'Potato Early Blight (Alternaria solani)',
    crop: 'Potato',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Fungal leaf disease causing brown ring-shaped lesions on older foliage, reducing photosynthetic area and tuber yield.',
    symptoms: ['Dark brown spots with yellow halos on lower leaves', 'Concentric rings inside leaf spots'],
    causes: ['High humidity, alternating wet and dry weather'],
    organicTreatments: ['Copper octanoate or Sulfur dust'],
    chemicalTreatments: ['Azoxystrobin or Difenoconazole spray'],
    prevention: ['Adequate nitrogen fertilization and drip irrigation'],
    recommendedProducts: [
      { name: 'CeyFungi Chlorothalonil 75', type: 'Fungicide', dosage: '2 g / Litre' },
    ],
  },

  // Grape Black Rot
  'Grape___Black_rot': {
    name: 'Grape___Black_rot',
    displayName: 'Grape Black Rot (Guignardia bidwellii)',
    crop: 'Grape',
    category: 'Fungal',
    severity: 'High',
    description: 'Black rot affects all green parts of the grapevine. Infected berries shrivel into hard, black, wrinkled mummies that cling to the vine cluster.',
    symptoms: [
      'Small reddish-brown circular spots on leaves',
      'Berries turning brown then black and shriveling into hard mummies',
    ],
    causes: ['Overwintering mummified berries on vines, warm wet spring weather'],
    organicTreatments: ['Liquid Copper or Lime Sulfur dormant spray'],
    chemicalTreatments: ['Myclobutanil or Tebeconazole systemic fungicides'],
    prevention: ['Prune vines to allow maximum sunlight and airflow through canopy'],
    recommendedProducts: [
      { name: 'GrapeShield Sulfur 80', type: 'Bio-Fungicide', dosage: '3 g / Litre' },
    ],
  },

  // Healthy Default Strategy
  'Healthy': {
    name: 'Healthy',
    displayName: 'Healthy Crop (No Pathogen Detected)',
    crop: 'Greenhouse Crop',
    category: 'Healthy',
    severity: 'Healthy',
    description: 'Your crop leaf displays vibrant green pigmentation, uniform cell structure, and no visible sign of fungal, bacterial, or viral plant disease.',
    symptoms: ['Vibrant, unblemished green leaves', 'Strong stem and leaf structural integrity'],
    causes: ['Optimal greenhouse environmental management and good nutrition'],
    organicTreatments: ['Maintain regular liquid seaweed organic fertilizer feeding'],
    chemicalTreatments: ['No chemical intervention required'],
    prevention: [
      'Continue routine soil testing and IPM monitoring',
      'Keep greenhouse temperature between 20°C–26°C',
    ],
    recommendedProducts: [
      { name: 'CeyGreen Organic Seaweed Liquid', type: 'Bio-Stimulant', dosage: '2 ml / Litre' },
    ],
  },
};

export const getDiseaseDetail = (diseaseName: string): DiseaseDetail => {
  if (!diseaseName) return DISEASE_KNOWLEDGE['Healthy'];

  // Match exact key
  if (DISEASE_KNOWLEDGE[diseaseName]) {
    return DISEASE_KNOWLEDGE[diseaseName];
  }

  // Check if healthy
  if (diseaseName.toLowerCase().includes('healthy')) {
    return DISEASE_KNOWLEDGE['Healthy'];
  }

  // Case-insensitive / partial match
  const matchKey = Object.keys(DISEASE_KNOWLEDGE).find((key) =>
    key.toLowerCase().includes(diseaseName.toLowerCase()) || diseaseName.toLowerCase().includes(key.toLowerCase())
  );

  if (matchKey && DISEASE_KNOWLEDGE[matchKey]) {
    return DISEASE_KNOWLEDGE[matchKey];
  }

  // Fallback generic detail for unmatched disease labels
  const cleanName = diseaseName.replace(/___/g, ' - ').replace(/_/g, ' ');
  const isBacterial = diseaseName.toLowerCase().includes('bacteri');
  const isViral = diseaseName.toLowerCase().includes('virus');

  return {
    name: diseaseName,
    displayName: cleanName,
    crop: 'Greenhouse Crop',
    category: isBacterial ? 'Bacterial' : isViral ? 'Viral' : 'Fungal',
    severity: isViral || isBacterial ? 'High' : 'Moderate',
    description: `AI plant pathology analysis detected ${cleanName}. Prompt agronomist intervention is recommended to prevent spread in greenhouse crops.`,
    symptoms: ['Foliar discoloration and localized tissue necrosis', 'Leaf spots or viral mottling on leaves'],
    causes: ['Favorable humidity, air movement, or vector transmission'],
    organicTreatments: ['Apply Neem oil 70% EC or Copper-based organic protectant spray'],
    chemicalTreatments: ['Consult local agricultural extensión agent for targeted systemic treatment'],
    prevention: ['Sanitize tools between handling plants', 'Maintain adequate plant spacing for airflow'],
    recommendedProducts: [
      { name: 'CeyBio CopperShield Liquid', type: 'Bactericide / Fungicide', dosage: '2 ml / Litre' },
    ],
  };
};
