export interface MicroclimateDrivers {
  temperatureRange: string;
  criticalHumidity: string;
  leafWetnessHours: string;
  vpdRiskLevel: 'Low' | 'Moderate' | 'High' | 'Extreme';
}

export interface DiseaseDetail {
  name: string;
  displayName: string;
  scientificName: string;
  crop: string;
  category: 'Fungal' | 'Bacterial' | 'Viral' | 'Pest' | 'Healthy';
  severity: 'Critical' | 'High' | 'Moderate' | 'Low' | 'Healthy';
  description: string;
  etiology: {
    pathogenType: string;
    incubationPeriod: string;
    transmissionVectors: string[];
    inoculumSource: string;
    hostInvasionMechanism: string;
  };
  symptoms: {
    leafMarkers: string[];
    canopyProgression: string;
    stemAndFruitSigns: string[];
    lookAlikes: string[];
  };
  microclimate: MicroclimateDrivers;
  preventionAndQuarantine: {
    sanitation: string[];
    cropRotation: string;
    airflowAndSpacing: string;
    scoutingCadence: string;
    quarantineAction: string;
  };
}

export const CROPS_LIST = [
  { id: 'Tomato', name: 'Tomato', icon: '🍅', diseases: ['Tomato___Early_blight', 'Tomato___Late_blight', 'Tomato___Bacterial_spot', 'Tomato___Leaf_Mold', 'Tomato___Septoria_leaf_spot', 'Tomato___healthy'] },
  { id: 'Potato', name: 'Potato', icon: '🥔', diseases: ['Potato___Early_blight', 'Potato___Late_blight', 'Potato___healthy'] },
  { id: 'Pepper', name: 'Bell Pepper', icon: '🫑', diseases: ['Pepper,_bell___Bacterial_spot', 'Pepper,_bell___healthy'] },
  { id: 'Grape', name: 'Grape', icon: '🍇', diseases: ['Grape___Black_rot', 'Grape___Esca_(Black_Measles)', 'Grape___healthy'] },
  { id: 'Strawberry', name: 'Strawberry', icon: '🍓', diseases: ['Strawberry___Leaf_scorch', 'Strawberry___healthy'] },
  { id: 'Apple', name: 'Apple', icon: '🍎', diseases: ['Apple___Black_rot', 'Apple___healthy'] },
  { id: 'Corn', name: 'Corn (Maize)', icon: '🌽', diseases: ['Corn___Common_rust', 'Corn___healthy'] },
];

export const DISEASE_KNOWLEDGE: Record<string, DiseaseDetail> = {
  // 1. Tomato Early Blight
  'Tomato___Early_blight': {
    name: 'Tomato___Early_blight',
    displayName: 'Tomato Early Blight',
    scientificName: 'Alternaria solani (Sorauer)',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'High',
    description: 'Alternaria solani is an aggressive necrotrophic fungal pathogen that primarily infects older lower foliage. It forms distinct concentric target-pattern lesions encircled by chlorotic yellow halos, causing extensive premature defoliation and severe yield loss.',
    etiology: {
      pathogenType: 'Deuteromycete Ascomycota Fungi',
      incubationPeriod: '3 to 5 days under optimal thermal conditions',
      transmissionVectors: ['Wind-blown macroconidia spores', 'Irrigation water splash', 'Infected pruning shears & handling'],
      inoculumSource: 'Overwinters in solanaceous crop residues, volunteer solanaceous weeds, and upper soil profile.',
      hostInvasionMechanism: 'Conidia germinate on wet leaf cuticle, penetrating directly through stomatal openings or mechanical micro-wounds.',
    },
    symptoms: {
      leafMarkers: [
        'Dark brown to black circular lesions exhibiting distinctive concentric ring "bullseye" patterns.',
        'Prominent chlorotic yellow halos surrounding expanding necrotic lesions.',
        'Premature senescence starting on lower senescing foliage and progressing upward.',
      ],
      canopyProgression: 'Ascending progression from lower ground-level foliage toward upper terminal canopy.',
      stemAndFruitSigns: [
        'Sunken, leathery dark collar rot lesions on lower stems near soil line.',
        'Dark sunken leathery decay near calyx fruit attachment.',
      ],
      lookAlikes: ['Septoria Leaf Spot (smaller lesions with pycnidia)', 'Magnesium deficiency (interveinal yellowing without necrotic targets)'],
    },
    microclimate: {
      temperatureRange: '24°C – 29°C (Optimal germination)',
      criticalHumidity: '> 85% Relative Humidity',
      leafWetnessHours: 'Minimum 2–4 hours of continuous leaf wetness',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Prune and destroy all lower foliage within 30 cm of soil line.',
        'Remove crop debris and perform thermal solarization between growing seasons.',
        'Sterilize pruning shears between rows using 70% isopropyl alcohol or 10% bleach.',
      ],
      cropRotation: 'Minimum 3-year rotational cycle away from Solanaceae (tomatoes, potatoes, eggplants).',
      airflowAndSpacing: 'Maintain 60 cm minimum inter-plant spacing and operate horizontal airflow fans continuously.',
      scoutingCadence: 'Inspect lower canopy twice weekly, particularly following high-humidity mornings.',
      quarantineAction: 'Isolate affected greenhouse row, reduce overhead misting, and immediately bag infected leaf samples.',
    },
  },

  // 2. Tomato Late Blight
  'Tomato___Late_blight': {
    name: 'Tomato___Late_blight',
    displayName: 'Tomato Late Blight',
    scientificName: 'Phytophthora infestans (Mont.) de Bary',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'Phytophthora infestans is a catastrophic water-mold oomycete capable of total canopy destruction within 7–10 days. It thrives in cool, saturated microclimates, rapidly devastating green foliage, petioles, and developing fruit.',
    etiology: {
      pathogenType: 'Oomycete (Water Mold Pathogen)',
      incubationPeriod: '2 to 4 days during high-moisture periods',
      transmissionVectors: ['Wind-driven airborne sporangia (traveling up to 20 km)', 'Water splash', 'Infected transplant seedlings'],
      inoculumSource: 'Living host tissues, infected greenhouse cull piles, and volunteer potato tubers.',
      hostInvasionMechanism: 'Biflagellate zoospores release in water films, encyst, and produce germ tubes that pierce epidermal cells via appressoria.',
    },
    symptoms: {
      leafMarkers: [
        'Rapidly expanding, irregular pale green to dark water-soaked greasy lesions.',
        'White delicate downy fungal sporulation on abaxial (underside) leaf surface during humid mornings.',
        'Rapid wilting, collapse, and blackened necrosis of entire leaf leaflets.',
      ],
      canopyProgression: 'Explosive systemic collapse across entire canopy within 48–72 hours.',
      stemAndFruitSigns: [
        'Dark brown, greasy-looking lesions girdling petioles and main stems.',
        'Golden-brown to chocolate-brown firm leathery rot on green fruit surfaces.',
      ],
      lookAlikes: ['Frost damage (lacks abaxial white sporulation)', 'Blossom End Rot (confined strictly to bottom blossom end of fruit)'],
    },
    microclimate: {
      temperatureRange: '15°C – 22°C (Cool, damp environments)',
      criticalHumidity: '> 90% Relative Humidity',
      leafWetnessHours: '6–8 hours of uninterrupted free moisture',
      vpdRiskLevel: 'Extreme',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Immediately destroy, bag, and remove all infected plants from the greenhouse.',
        'Never compost infected material; incinerate or dispose of in sealed landfill containers.',
        'Eradicate all volunteer potato and solanaceous plants within 500 meters of the greenhouse.',
      ],
      cropRotation: 'Mandatory non-host rotation with all solanaceous greenhouse varieties.',
      airflowAndSpacing: 'Run ridge ventilators and heating coils simultaneously to purge nocturnal greenhouse moisture spikes.',
      scoutingCadence: 'Daily morning scouting of abaxial leaf surfaces and canopy perimeter.',
      quarantineAction: 'Immediate quarantine of affected greenhouse zone; restrict staff movements between houses.',
    },
  },

  // 3. Tomato Bacterial Spot
  'Tomato___Bacterial_spot': {
    name: 'Tomato___Bacterial_spot',
    displayName: 'Tomato Bacterial Spot',
    scientificName: 'Xanthomonas perforans / euvesicatoria',
    crop: 'Tomato',
    category: 'Bacterial',
    severity: 'High',
    description: 'Xanthomonas species cause widespread necrotic lesions across leaf blades, stems, and fruit. In warm, humid greenhouse environments, lesions coalesce, leading to extensive blossom drop, defoliation, and fruit unmarketability.',
    etiology: {
      pathogenType: 'Gram-Negative Bacterium',
      incubationPeriod: '5 to 7 days',
      transmissionVectors: ['Overhead water splash', 'Handling wet plants', 'Contaminated commercial seed stocks'],
      inoculumSource: 'Contaminated seed coats, crop debris, and greenhouse equipment surfaces.',
      hostInvasionMechanism: 'Bacterial cells enter through natural stomatal pores, hydathodes, and micro-abrasions caused by pruning or trellising.',
    },
    symptoms: {
      leafMarkers: [
        'Numerous small (1–3 mm) water-soaked angular spots turning dark brown/black.',
        'Lesion centers dry out and drop out, creating a characteristic "shot-hole" appearance.',
        'Severe yellowing and blighting of surrounding foliage.',
      ],
      canopyProgression: 'Spreads rapidly across upper and middle canopy following irrigation or handling.',
      stemAndFruitSigns: [
        'Elongated dark cankers on stems.',
        'Small, raised blister-like spots on green fruit that become brown, scabby, and sunken.',
      ],
      lookAlikes: ['Bacterial Speck (Pseudomonas syringae — smaller round speck lesions)', 'Target Spot (larger concentric fungal lesions)'],
    },
    microclimate: {
      temperatureRange: '25°C – 32°C (Warm, tropical greenhouse conditions)',
      criticalHumidity: '> 80% Relative Humidity',
      leafWetnessHours: '1–2 hours of leaf surface moisture',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Use hot-water treated or certified disease-free pathogen-indexed seeds.',
        'Strictly avoid handling, pinching, or trellising wet plant canopies.',
        'Sanitize greenhouse structural benches and trays with quaternary ammonium.',
      ],
      cropRotation: '2-year rotation away from tomatoes and peppers.',
      airflowAndSpacing: 'Strict drip irrigation only; eliminate any overhead misting systems.',
      scoutingCadence: 'Bi-weekly systematic crop canopy inspection.',
      quarantineAction: 'Mark affected zone; sanitize worker gloves and tools before accessing healthy blocks.',
    },
  },

  // 4. Tomato Leaf Mold
  'Tomato___Leaf_Mold': {
    name: 'Tomato___Leaf_Mold',
    displayName: 'Tomato Leaf Mold',
    scientificName: 'Passalora fulva (syn. Fulvia fulva)',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Passalora fulva is a greenhouse-specific fungal foliar disease. It manifests as pale green to yellowish chlorotic patches on upper leaf surfaces, accompanied by dense olive-green velvety mold colonies beneath.',
    etiology: {
      pathogenType: 'Hyphomycete Fungi',
      incubationPeriod: '10 to 14 days',
      transmissionVectors: ['Air currents within greenhouse', 'Worker clothing', 'Insects & tools'],
      inoculumSource: 'Conidia surviving on greenhouse framework, crop debris, and soil.',
      hostInvasionMechanism: 'Conidial germ tubes enter exclusively through leaf stomata on the lower abaxial surface.',
    },
    symptoms: {
      leafMarkers: [
        'Diffuse pale yellow to light green chlorotic patches on adaxial (upper) leaf surfaces.',
        'Olive-green to velvety brown velvety sporulating mold layer on abaxial (underside) surfaces.',
        'Infected leaves turn yellow, curl upward, wither, and drop prematurely.',
      ],
      canopyProgression: 'Originates on older lower canopy leaves with dense foliage, moving steadily upward.',
      stemAndFruitSigns: ['Rarely infects fruit directly; blossoms may wither and drop prematurely.'],
      lookAlikes: ['Powdery Mildew (produces white powdery patches on both leaf surfaces)'],
    },
    microclimate: {
      temperatureRange: '21°C – 24°C (Moderate greenhouse temps)',
      criticalHumidity: '> 85% Relative Humidity (Optimal >90%)',
      leafWetnessHours: 'Does not require free water if relative humidity remains >85%',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: ['Maintain heating and active fan ventilation to keep nocturnal RH strictly below 85%.'],
      cropRotation: 'Clean crop turnover and deployment of resistant F1 cultivars with Cf gene resistance.',
      airflowAndSpacing: 'De-leaf lower canopy to encourage cross-canopy air velocity exceeding 0.5 m/s.',
      scoutingCadence: 'Weekly inspection of shaded canopy zones and lower abaxial leaf surfaces.',
      quarantineAction: 'Ventilate greenhouse immediately to purge ambient humidity below threshold.',
    },
  },

  // 5. Potato Late Blight
  'Potato___Late_blight': {
    name: 'Potato___Late_blight',
    displayName: 'Potato Late Blight',
    scientificName: 'Phytophthora infestans (Mont.) de Bary',
    crop: 'Potato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'The historic late blight pathogen devastating potato foliage, stems, and subsurface tubers. Capable of causing total tuber rot in storage if spores are washed into the soil.',
    etiology: {
      pathogenType: 'Oomycete Pathogen',
      incubationPeriod: '3 to 5 days',
      transmissionVectors: ['Airborne sporangia', 'Infected seed tubers', 'Rain splash'],
      inoculumSource: 'Infected seed potatoes, discard cull piles, and volunteer potato tubers.',
      hostInvasionMechanism: 'Direct cuticle penetration by appressoria and intercellular hyphal growth.',
    },
    symptoms: {
      leafMarkers: [
        'Water-soaked dark lesions on leaf tips, margins, and petiole joints.',
        'White fungal bloom on abaxial leaf surfaces in humid morning air.',
        'Rapid necrosis and blackening of foliage with rotting odor.',
      ],
      canopyProgression: 'Rapid field-wide foliar collapse within 5 to 7 days.',
      stemAndFruitSigns: [
        'Dark brown necrotic stem lesions leading to stem breakage.',
        'Reddish-brown dry granular decay extending into tuber flesh.',
      ],
      lookAlikes: ['Potato Early Blight (dry concentric target spots, no white mold bloom)'],
    },
    microclimate: {
      temperatureRange: '13°C – 21°C (Cool, damp weather)',
      criticalHumidity: '> 90% Relative Humidity',
      leafWetnessHours: '5–8 hours continuous leaf moisture',
      vpdRiskLevel: 'Extreme',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Plant only certified pathogen-free seed tubers.',
        'Hill soil high over tubers (10–15 cm) to create a barrier preventing spore runoff into soil.',
        'Kill canopy haulms 2 weeks before harvest to prevent tuber contamination.',
      ],
      cropRotation: 'Strict 3-year rotation away from all solanaceous crops.',
      airflowAndSpacing: 'Ensure proper hill spacing for canopy drainage.',
      scoutingCadence: 'Daily morning scouting during cool, overcast weather conditions.',
      quarantineAction: 'Remove and destroy entire infected hill including all seed tubers.',
    },
  },

  // 6. Potato Early Blight
  'Potato___Early_blight': {
    name: 'Potato___Early_blight',
    displayName: 'Potato Early Blight',
    scientificName: 'Alternaria solani',
    crop: 'Potato',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Common foliar pathogen causing premature leaf senescence and yield reduction in mature potato crops, particularly following periods of plant stress.',
    etiology: {
      pathogenType: 'Deuteromycete Fungus',
      incubationPeriod: '4 to 6 days',
      transmissionVectors: ['Wind-borne conidia', 'Soil splash', 'Equipment'],
      inoculumSource: 'Crop residues and infected solanaceous debris.',
      hostInvasionMechanism: 'Stomatal entry and direct cuticle penetration on stressed foliage.',
    },
    symptoms: {
      leafMarkers: [
        'Dark brown angular spots with characteristic concentric rings.',
        'Yellow chlorotic halos surrounding lesions.',
        'Lower leaf drying and upward curling.',
      ],
      canopyProgression: 'Slow upward progression as crop matures and enters tuber bulking.',
      stemAndFruitSigns: ['Brown to black corky sunken lesions on tuber skin.'],
      lookAlikes: ['Brown Spot (Alternaria alternata — smaller non-target spots)'],
    },
    microclimate: {
      temperatureRange: '22°C – 28°C',
      criticalHumidity: '> 80% RH',
      leafWetnessHours: '2–4 hours',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: ['Maintain optimal nitrogen and potassium fertilization to prevent premature canopy stress.'],
      cropRotation: '3-year rotation with non-solanaceous crops.',
      airflowAndSpacing: 'Drip irrigation to avoid leaf wetness.',
      scoutingCadence: 'Weekly canopy scouting starting at mid-season flowering.',
      quarantineAction: 'Prune affected lower leaves and monitor surrounding rows.',
    },
  },

  // 7. Pepper Bacterial Spot
  'Pepper,_bell___Bacterial_spot': {
    name: 'Pepper,_bell___Bacterial_spot',
    displayName: 'Bell Pepper Bacterial Spot',
    scientificName: 'Xanthomonas campestris pv. vesicatoria',
    crop: 'Bell Pepper',
    category: 'Bacterial',
    severity: 'High',
    description: 'Devastating bacterial disease of sweet and hot peppers. Produces small water-soaked foliar lesions that become necrotic and induce severe leaf drop, exposing fruit to sunscald.',
    etiology: {
      pathogenType: 'Gram-Negative Bacterium',
      incubationPeriod: '4 to 7 days',
      transmissionVectors: ['Water splash', 'Worker tools', 'Infected seed'],
      inoculumSource: 'Infected seed coats, crop residues, solanaceous weed hosts.',
      hostInvasionMechanism: 'Enters through stomata and wounding on leaf cuticle.',
    },
    symptoms: {
      leafMarkers: [
        'Small (1–3 mm) water-soaked circular to irregular spots turning dark brown.',
        'Lesion margins often have a yellow-green halo.',
        'Extensive defoliation leaving stems bare.',
      ],
      canopyProgression: 'Spreads rapidly following wet weather or greenhouse condensation.',
      stemAndFruitSigns: [
        'Canker lesions on stems.',
        'Raised, blister-like rough scabby warts on green pepper fruit.',
      ],
      lookAlikes: ['Bacterial Canker (Clavibacter michiganensis)'],
    },
    microclimate: {
      temperatureRange: '24°C – 30°C',
      criticalHumidity: '> 85% RH',
      leafWetnessHours: '1–3 hours',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: ['Use certified disease-free pepper seeds and resistant hybrids (Bs2/Bs3 gene).'],
      cropRotation: '2-year rotation with non-host crops like brassicas or corn.',
      airflowAndSpacing: 'Avoid overhead sprinkler systems; sanitize harvesting crates.',
      scoutingCadence: 'Twice weekly scouting of young vegetative growth.',
      quarantineAction: 'Remove severely infected plants; sanitize hands and pruning shears.',
    },
  },

  // 8. Grape Black Rot
  'Grape___Black_rot': {
    name: 'Grape___Black_rot',
    displayName: 'Grapevine Black Rot',
    scientificName: 'Guignardia bidwellii (Ellis) Viala & Ravaz',
    crop: 'Grape',
    category: 'Fungal',
    severity: 'High',
    description: 'Black rot attacks leaves, shoots, and young grape clusters. Infected berries rapidly turn brown, shrivel, and desiccate into hard, black, wrinkled "mummies" that cling to the vine.',
    etiology: {
      pathogenType: 'Ascomycete Fungus',
      incubationPeriod: '7 to 14 days',
      transmissionVectors: ['Ascospores released by rain', 'Conidia in water droplets'],
      inoculumSource: 'Overwintering mummified berries and shoot cankers on grapevines.',
      hostInvasionMechanism: 'Direct penetration of immature epidermal cells on young leaves and berries.',
    },
    symptoms: {
      leafMarkers: [
        'Small reddish-brown circular spots with dark brown margins on leaves.',
        'Tiny black pepper-like fruiting bodies (pycnidia) embedded inside lesions.',
        'Angular necrotic areas near leaf veins.',
      ],
      canopyProgression: 'Begins on young spring foliage and spreads to berry clusters after bloom.',
      stemAndFruitSigns: [
        'Black elongated lesions on shoots and tendrils.',
        'Berries turn light brown, soften, rapidly turn black, and shrivel into mummies.',
      ],
      lookAlikes: ['Anthracnose / Bird’s Eye Rot (lesions have grey centers with red-purple margins)'],
    },
    microclimate: {
      temperatureRange: '20°C – 27°C',
      criticalHumidity: '> 80% RH',
      leafWetnessHours: '6–10 hours of continuous moisture',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Dormant winter pruning of all mummified berries from vine trellises.',
        'Trellis training and canopy hedging to maximize sunlight and wind penetration.',
        'Destroy all infected prunings by burning or deep burial.',
      ],
      cropRotation: 'Permanent perennial planting — manage via canopy aeration and sanitation.',
      airflowAndSpacing: 'Canopy shoot thinning and leaf pulling around grape cluster zones.',
      scoutingCadence: 'Weekly inspection from pre-bloom through fruit veraison.',
      quarantineAction: 'Prune out infected cane shoots and remove dropped berry mummies.',
    },
  },

  // 9. Strawberry Leaf Scorch
  'Strawberry___Leaf_scorch': {
    name: 'Strawberry___Leaf_scorch',
    displayName: 'Strawberry Leaf Scorch',
    scientificName: 'Diplocarpon earlianum (Ellis & Everh.) F.A. Wolf',
    crop: 'Strawberry',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Common foliar disease of greenhouse and field strawberries. Produces numerous irregular purplish blotches that coalesce, causing the leaf tissue to turn reddish-brown and appear scorched.',
    etiology: {
      pathogenType: 'Ascomycota Fungi',
      incubationPeriod: '6 to 10 days',
      transmissionVectors: ['Water splash', 'Wind-blown conidia', 'Handling runner plants'],
      inoculumSource: 'Overwinters on living green leaves and plant crowns.',
      hostInvasionMechanism: 'Appressoria directly pierce the leaf cuticle on either leaf surface.',
    },
    symptoms: {
      leafMarkers: [
        'Numerous small, irregular purple to dark brown blotches across leaf blades.',
        'Absence of white centers (distinguishing from Common Leaf Spot).',
        'Leaf margins curl upward and take on a scorched, burnt appearance.',
      ],
      canopyProgression: 'Spreads across all foliage, reducing plant vigor and runner production.',
      stemAndFruitSigns: ['Purple-brown streaks on petioles and fruit stems causing fruit withering.'],
      lookAlikes: ['Common Leaf Spot (Mycosphaerella fragariae — lesions have distinctive white centers)'],
    },
    microclimate: {
      temperatureRange: '18°C – 25°C',
      criticalHumidity: '> 85% RH',
      leafWetnessHours: '8–12 hours of leaf wetness',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: ['Remove and destroy older scorched leaves when renovating greenhouse strawberry beds.'],
      cropRotation: 'Use clean certified nursery runner stock.',
      airflowAndSpacing: 'Space table-top gutters or raised beds to promote leaf drainage.',
      scoutingCadence: 'Bi-weekly scouting of new crown flushes.',
      quarantineAction: 'Trim severely scorched leaves and clean gutters.',
    },
  },

  // 10. Healthy Crop
  'Healthy': {
    name: 'Healthy',
    displayName: 'Healthy Crop (No Disease Pathogen Detected)',
    scientificName: 'Physiologically Optimal Foliage',
    crop: 'Greenhouse Crop',
    category: 'Healthy',
    severity: 'Healthy',
    description: 'Your crop leaf displays vibrant green pigmentation, uniform cellular structure, optimal chlorophyll fluorescence, and zero detectable viral, bacterial, or fungal pathogens.',
    etiology: {
      pathogenType: 'None (Healthy Plant Tissue)',
      incubationPeriod: 'N/A',
      transmissionVectors: ['None'],
      inoculumSource: 'None (Clean environmental management)',
      hostInvasionMechanism: 'Plant natural cuticular immunity and systemic acquired resistance (SAR) intact.',
    },
    symptoms: {
      leafMarkers: [
        'Vibrant, deep green leaf pigmentation with uniform chloroplast distribution.',
        'Clean, unblemished epidermal cuticle without necrotic or chlorotic lesions.',
        'Turgid petioles with robust vascular vein network and healthy stomata.',
      ],
      canopyProgression: 'Uniform canopy growth with balanced vegetative-to-generative balance.',
      stemAndFruitSigns: ['Healthy firm stems with strong nodal spacing; unblemished developing fruit.'],
      lookAlikes: ['None'],
    },
    microclimate: {
      temperatureRange: '20°C – 26°C (Target Greenhouse Climate)',
      criticalHumidity: '65% – 75% RH (Ideal Safe Band)',
      leafWetnessHours: '0 hours (Dry Canopy surfaces)',
      vpdRiskLevel: 'Low',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Maintain existing strict greenhouse biosecurity and footbath protocols.',
        'Continue balanced fertigation recipe and bi-weekly leaf tissue testing.',
        'Keep automated climate sensors calibrated for accurate temperature and humidity readings.',
      ],
      cropRotation: 'Standard planned crop cycling.',
      airflowAndSpacing: 'Maintain horizontal airflow to maintain uniform microclimate.',
      scoutingCadence: 'Routine weekly scouting across all greenhouse zones.',
      quarantineAction: 'No quarantine needed; continue standard agronomic practices.',
    },
  },
};

export const getDiseaseDetail = (diseaseName: string): DiseaseDetail => {
  if (!diseaseName) return DISEASE_KNOWLEDGE['Healthy'];

  if (DISEASE_KNOWLEDGE[diseaseName]) {
    return DISEASE_KNOWLEDGE[diseaseName];
  }

  if (diseaseName.toLowerCase().includes('healthy')) {
    return DISEASE_KNOWLEDGE['Healthy'];
  }

  const matchKey = Object.keys(DISEASE_KNOWLEDGE).find(
    (key) =>
      key.toLowerCase().includes(diseaseName.toLowerCase()) ||
      diseaseName.toLowerCase().includes(key.toLowerCase())
  );

  if (matchKey && DISEASE_KNOWLEDGE[matchKey]) {
    return DISEASE_KNOWLEDGE[matchKey];
  }

  // Fallback clinical profile
  const cleanName = diseaseName.replace(/___/g, ' - ').replace(/_/g, ' ');
  const isBacterial = diseaseName.toLowerCase().includes('bacteri');
  const isViral = diseaseName.toLowerCase().includes('virus');
  const isPest = diseaseName.toLowerCase().includes('mite') || diseaseName.toLowerCase().includes('pest');

  return {
    name: diseaseName,
    displayName: cleanName,
    scientificName: isBacterial ? 'Bacterial Phytopathogen' : isViral ? 'Viral Plant Complex' : isPest ? 'Arthropod Pest Vector' : 'Fungal Foliar Pathogen',
    crop: 'Greenhouse Crop',
    category: isBacterial ? 'Bacterial' : isViral ? 'Viral' : isPest ? 'Pest' : 'Fungal',
    severity: isViral || isBacterial ? 'High' : 'Moderate',
    description: `AI neural network pathology classification identified markers consistent with ${cleanName}. Prompt clinical agronomist intervention and foliar isolation is recommended.`,
    etiology: {
      pathogenType: isBacterial ? 'Phytopathogenic Bacterium' : isViral ? 'Plant Virus Agent' : 'Foliar Fungal Pathogen',
      incubationPeriod: '4 to 8 days',
      transmissionVectors: ['Foliar moisture splash', 'Air currents', 'Handling tools'],
      inoculumSource: 'Crop debris and environmental spore reservoirs.',
      hostInvasionMechanism: 'Cuticular penetration or natural stomatal opening entry.',
    },
    symptoms: {
      leafMarkers: [
        'Foliar discoloration with localized tissue necrosis or chlorosis.',
        'Irregular spot formation or viral vein clearing on leaf surface.',
      ],
      canopyProgression: 'Spreads through adjacent canopy foliage under favorable microclimate.',
      stemAndFruitSigns: ['Possible lesion formation on petioles or fruit surfaces.'],
      lookAlikes: ['Nutrient imbalance or abiotic environmental stress'],
    },
    microclimate: {
      temperatureRange: '20°C – 28°C',
      criticalHumidity: '> 80% RH',
      leafWetnessHours: '2–4 hours',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Prune affected leaves with sanitized tools.',
        'Sanitize greenhouse work surfaces and tools.',
        'Avoid handling plants when canopy is damp.',
      ],
      cropRotation: 'Standard multi-year crop rotation.',
      airflowAndSpacing: 'Maintain adequate plant spacing and active greenhouse ventilation.',
      scoutingCadence: 'Bi-weekly systematic scouting.',
      quarantineAction: 'Isolate affected plants and monitor surrounding rows.',
    },
  };
};
