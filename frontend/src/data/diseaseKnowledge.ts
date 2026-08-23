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

export interface CropInfo {
  id: string;
  name: string;
  image: string;
  diseases: string[];
}

export const CROPS_LIST: CropInfo[] = [
  { id: 'Tomato', name: 'Tomato', image: '/images/crops/tomato.webp', diseases: ['Tomato___Early_blight', 'Tomato___Late_blight', 'Tomato___Bacterial_spot', 'Tomato___Leaf_Mold', 'Tomato___Septoria_leaf_spot', 'Tomato___healthy'] },
  { id: 'Potato', name: 'Potato', image: '/images/crops/potato.webp', diseases: ['Potato___Early_blight', 'Potato___Late_blight', 'Potato___healthy'] },
  { id: 'Pepper', name: 'Bell Pepper', image: '/images/crops/bell-pepper.webp', diseases: ['Pepper,_bell___Bacterial_spot', 'Pepper,_bell___healthy'] },
  { id: 'Grape', name: 'Grape', image: '/images/crops/grape.webp', diseases: ['Grape___Black_rot', 'Grape___Esca_(Black_Measles)', 'Grape___healthy'] },
  { id: 'Strawberry', name: 'Strawberry', image: '/images/crops/strawberry.webp', diseases: ['Strawberry___Leaf_scorch', 'Strawberry___healthy'] },
  { id: 'Chillie', name: 'Chillie', image: '/images/crops/chillie.png', diseases: ['Chilli___Leaf_curl', 'Chilli___Anthracnose', 'Chilli___Bacterial_leaf_spot', 'Chilli___healthy'] },
  { id: 'Corn', name: 'Corn (Maize)', image: '/images/crops/corn.webp', diseases: ['Corn___Common_rust', 'Corn___healthy'] },
];

export const DISEASE_KNOWLEDGE: Record<string, DiseaseDetail> = {
  // 1. Tomato Early Blight
  'Tomato___Early_blight': {
    name: 'Tomato___Early_blight',
    displayName: 'Tomato Early Blight',
    scientificName: 'Alternaria solani',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'High',
    description: 'Early Blight is a common fungus that attacks older lower leaves first. It creates dark brown spots with distinctive circular ring patterns (like a target) surrounded by yellow halos, causing leaves to turn yellow and drop off.',
    etiology: {
      pathogenType: 'Foliar Fungal Disease',
      incubationPeriod: '3 to 5 days after contact with moisture',
      transmissionVectors: ['Wind-blown spores', 'Splashing rain or irrigation water', 'Dirty tools and handling'],
      inoculumSource: 'Lives in old crop debris, infected soil, and weeds around the greenhouse.',
      hostInvasionMechanism: 'Fungal spores land on wet leaves, sprout in water droplets, and penetrate leaf pores.',
    },
    symptoms: {
      leafMarkers: [
        'Dark brown circular spots with target-like concentric rings on lower leaves.',
        'Yellow halos surrounding dark spots causing premature leaf drop.',
        'Older lower leaves turning completely yellow and drying out.',
      ],
      canopyProgression: 'Starts on bottom leaves near the soil and moves upward through the plant canopy.',
      stemAndFruitSigns: [
        'Sunken, dark dry collar spots on stems near the soil line.',
        'Dark sunken leathery decay near the stem-end of ripe or green tomatoes.',
      ],
      lookAlikes: ['Septoria Leaf Spot (has smaller spots with tiny black dots)', 'Magnesium deficiency (yellowing between leaf veins without brown target rings)'],
    },
    microclimate: {
      temperatureRange: '24°C – 29°C (Warm greenhouse weather)',
      criticalHumidity: '> 80% Relative Humidity',
      leafWetnessHours: '2–4 hours of wet leaf surfaces',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Prune and remove all bottom leaves within 30 cm of the ground.',
        'Always clean pruning shears with disinfectant alcohol between plant rows.',
        'Immediately bag and remove trimmed infected leaves from the greenhouse.',
      ],
      cropRotation: 'Do not plant tomatoes, potatoes, or eggplants in the same soil for at least 3 seasons.',
      airflowAndSpacing: 'Space plants at least 50–60 cm apart and keep ventilation fans running for airflow.',
      scoutingCadence: 'Check lower leaves twice a week, especially after humid or cloudy mornings.',
      quarantineAction: 'Isolate affected rows, avoid overhead watering, and prune infected lower leaves immediately.',
    },
  },

  // 2. Tomato Late Blight
  'Tomato___Late_blight': {
    name: 'Tomato___Late_blight',
    displayName: 'Tomato Late Blight',
    scientificName: 'Phytophthora infestans',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'Late Blight is a rapid, high-risk disease that thrives in cool, humid conditions. It produces large water-soaked greasy brown spots on leaves with white fuzzy growth underneath, and can destroy an entire greenhouse crop in 7 to 10 days if unchecked.',
    etiology: {
      pathogenType: 'Water-Mold Fungal Pathogen',
      incubationPeriod: '2 to 4 days in cool, humid air',
      transmissionVectors: ['Wind-borne spores travelling across fields', 'Splashing water', 'Infected seedlings'],
      inoculumSource: 'Survives in cull piles, volunteer potato tubers, and living green host tissue.',
      hostInvasionMechanism: 'Swimming spores quickly bore into leaf tissue when leaves stay wet for hours.',
    },
    symptoms: {
      leafMarkers: [
        'Large, irregular dark grey to greasy water-soaked patches on leaves.',
        'White fuzzy mold on the underside of leaves during humid morning hours.',
        'Leaves rapidly turning black, wilting, and rotting with a distinct smell.',
      ],
      canopyProgression: 'Rapid collapse of entire branches and canopy within 48 to 72 hours.',
      stemAndFruitSigns: [
        'Dark brown greasy lesions that girdle stems and branch joints.',
        'Firm, brown leathery rot patches spreading across green tomatoes.',
      ],
      lookAlikes: ['Cold/Frost injury (does not produce white fuzzy mold on leaf undersides)', 'Blossom End Rot (occurs only on the bottom blossom tip of the fruit)'],
    },
    microclimate: {
      temperatureRange: '15°C – 22°C (Cool, damp conditions)',
      criticalHumidity: '> 85% Relative Humidity',
      leafWetnessHours: '6–8 hours of wet leaves',
      vpdRiskLevel: 'Extreme',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Immediately remove and destroy all infected plants in sealed plastic bags.',
        'Never throw infected tomato waste in open compost piles; burn or bury deeply.',
        'Eliminate any wild volunteer potatoes growing near the greenhouse.',
      ],
      cropRotation: 'Rotate out of solanaceous crops (tomatoes, potatoes) for multiple seasons.',
      airflowAndSpacing: 'Open ridge vents and run heaters in early morning to clear condensation.',
      scoutingCadence: 'Inspect leaf undersides every morning during cool, overcast weather.',
      quarantineAction: 'Quarantine the affected greenhouse section and restrict workers from moving spores to healthy bays.',
    },
  },

  // 3. Tomato Bacterial Spot
  'Tomato___Bacterial_spot': {
    name: 'Tomato___Bacterial_spot',
    displayName: 'Tomato Bacterial Spot',
    scientificName: 'Xanthomonas perforans',
    crop: 'Tomato',
    category: 'Bacterial',
    severity: 'High',
    description: 'Bacterial Spot causes hundreds of tiny black specks across leaves and stems. The spots dry out and fall out, giving leaves a "shot-hole" appearance, causing severe leaf drop and exposing fruit to sunscald.',
    etiology: {
      pathogenType: 'Bacterial Plant Infection',
      incubationPeriod: '4 to 7 days',
      transmissionVectors: ['Overhead water splash', 'Handling wet plants', 'Contaminated seeds'],
      inoculumSource: 'Infected seeds, plant debris, and greenhouse bench surfaces.',
      hostInvasionMechanism: 'Bacteria enter through natural leaf breathing pores (stomata) and tiny pruning cuts.',
    },
    symptoms: {
      leafMarkers: [
        'Numerous tiny (1–3 mm) dark brown or black angular spots on leaves.',
        'Centers of old spots fall out, leaving small holes in the leaf blades.',
        'Yellowing of foliage around heavy clusters of spots.',
      ],
      canopyProgression: 'Spreads rapidly across the canopy after watering or working with wet plants.',
      stemAndFruitSigns: [
        'Dark elongated streaks on stems.',
        'Small raised rough brown scabby spots on green tomatoes.',
      ],
      lookAlikes: ['Bacterial Speck (smaller pinpoint specks)', 'Target Spot (larger round fungal rings)'],
    },
    microclimate: {
      temperatureRange: '25°C – 32°C (Warm, tropical weather)',
      criticalHumidity: '> 75% Relative Humidity',
      leafWetnessHours: '1–2 hours of surface water',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Use certified disease-free treated seeds.',
        'Never work in the crop, prune, or harvest while leaves are wet.',
        'Disinfect greenhouse trays and tools with sanitizing wash.',
      ],
      cropRotation: 'Rotate with non-host crops like sweet corn or cabbage.',
      airflowAndSpacing: 'Use drip irrigation only to keep plant leaves completely dry.',
      scoutingCadence: 'Check young leaves twice a week for tiny black dots.',
      quarantineAction: 'Mark infected plants; sanitize hands and tools before touching healthy plants.',
    },
  },

  // 4. Tomato Leaf Mold
  'Tomato___Leaf_Mold': {
    name: 'Tomato___Leaf_Mold',
    displayName: 'Tomato Leaf Mold',
    scientificName: 'Passalora fulva',
    crop: 'Tomato',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Leaf Mold is a common greenhouse fungus that causes pale yellow patches on top of leaves with olive-green velvety mold underneath. It thrives in humid, shaded greenhouse areas with poor air movement.',
    etiology: {
      pathogenType: 'Greenhouse Foliar Fungus',
      incubationPeriod: '10 to 14 days',
      transmissionVectors: ['Air currents inside the greenhouse', 'Worker clothing', 'Equipment'],
      inoculumSource: 'Old crop residues and greenhouse structural walls.',
      hostInvasionMechanism: 'Spores germinate and enter pores on the underside of leaves.',
    },
    symptoms: {
      leafMarkers: [
        'Pale yellow patches on the upper surface of leaves.',
        'Olive-green to velvety brown mold growing directly beneath the yellow patches.',
        'Infected leaves curl up, wither, and die prematurely.',
      ],
      canopyProgression: 'Starts in dense, shaded lower branches and moves up the plant.',
      stemAndFruitSigns: ['Flowers may wither and drop off, reducing fruit count.'],
      lookAlikes: ['Powdery Mildew (white powder on both top and bottom of leaves)'],
    },
    microclimate: {
      temperatureRange: '21°C – 25°C',
      criticalHumidity: '> 85% Relative Humidity',
      leafWetnessHours: 'High humidity is enough; does not require standing water',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: ['Keep greenhouse exhaust fans on to lower humidity below 80%.'],
      cropRotation: 'Plant resistant tomato varieties suited for greenhouse production.',
      airflowAndSpacing: 'Prune dense lower leaves to let fresh air and sunlight reach inside.',
      scoutingCadence: 'Inspect lower leaf undersides once a week in dense areas.',
      quarantineAction: 'Increase ventilation immediately and remove heavily infected leaves.',
    },
  },

  // 5. Potato Late Blight
  'Potato___Late_blight': {
    name: 'Potato___Late_blight',
    displayName: 'Potato Late Blight',
    scientificName: 'Phytophthora infestans',
    crop: 'Potato',
    category: 'Fungal',
    severity: 'Critical',
    description: 'Late blight attacks potato leaves, stems, and underground tubers. It causes rapid blackening and rotting of leaves with white mold underneath, and can rot entire potato harvests in storage.',
    etiology: {
      pathogenType: 'Water-Mold Pathogen',
      incubationPeriod: '3 to 5 days',
      transmissionVectors: ['Airborne spores', 'Infected seed potatoes', 'Water splash'],
      inoculumSource: 'Infected seed tubers, discard piles, and volunteer potato sprouts.',
      hostInvasionMechanism: 'Spores land on wet leaves, enter skin, and grow through plant veins.',
    },
    symptoms: {
      leafMarkers: [
        'Water-soaked dark brown to black patches on leaf edges.',
        'White downy mold on the underside of leaves in wet morning air.',
        'Rapid rotting of leaves with a bad smell.',
      ],
      canopyProgression: 'Whole field canopy can turn black within 5 to 7 days.',
      stemAndFruitSigns: [
        'Dark brown rotting streaks on stems causing plant collapse.',
        'Reddish-brown dry rot spreading inside potato tubers.',
      ],
      lookAlikes: ['Potato Early Blight (dry concentric target spots, no white downy mold)'],
    },
    microclimate: {
      temperatureRange: '13°C – 21°C (Cool, wet weather)',
      criticalHumidity: '> 85% Relative Humidity',
      leafWetnessHours: '5–8 hours of wet leaves',
      vpdRiskLevel: 'Extreme',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Plant only certified clean disease-free seed potatoes.',
        'Build high soil mounds over growing tubers to shield them from spores.',
        'Cut and destroy dead vines 2 weeks before harvesting tubers.',
      ],
      cropRotation: 'Wait 3 years before planting potatoes or tomatoes in the same plot.',
      airflowAndSpacing: 'Leave plenty of space between potato rows for good sun exposure.',
      scoutingCadence: 'Daily morning checks during cool, rainy periods.',
      quarantineAction: 'Remove and destroy entire infected potato plants including tubers.',
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
    description: 'Early Blight causes brown circular spots with concentric rings on older potato leaves, reducing plant strength and lowering tuber yields as crops mature.',
    etiology: {
      pathogenType: 'Foliar Fungus',
      incubationPeriod: '4 to 6 days',
      transmissionVectors: ['Wind-blown spores', 'Soil splash', 'Tractors/Tools'],
      inoculumSource: 'Old crop leaves in soil and nearby infected fields.',
      hostInvasionMechanism: 'Enters leaf pores on stressed or older potato foliage.',
    },
    symptoms: {
      leafMarkers: [
        'Dark brown spots with target-like rings on lower leaves.',
        'Yellowing of leaves around the dark spots.',
        'Lower leaves drying and curling up.',
      ],
      canopyProgression: 'Slowly spreads upward as potato plants grow larger.',
      stemAndFruitSigns: ['Brown to black corky sunken marks on tuber skin.'],
      lookAlikes: ['Brown Spot (smaller spots without distinct target rings)'],
    },
    microclimate: {
      temperatureRange: '22°C – 28°C',
      criticalHumidity: '> 75% RH',
      leafWetnessHours: '2–4 hours',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: ['Maintain good fertilizer feeding (nitrogen and potassium) to keep plants strong.'],
      cropRotation: '3-year rotation away from potatoes and tomatoes.',
      airflowAndSpacing: 'Drip irrigation to keep leaves dry.',
      scoutingCadence: 'Weekly crop checks starting when plants begin flowering.',
      quarantineAction: 'Prune affected bottom leaves and monitor nearby rows.',
    },
  },

  // 7. Pepper Bacterial Spot
  'Pepper,_bell___Bacterial_spot': {
    name: 'Pepper,_bell___Bacterial_spot',
    displayName: 'Bell Pepper Bacterial Spot',
    scientificName: 'Xanthomonas campestris',
    crop: 'Bell Pepper',
    category: 'Bacterial',
    severity: 'High',
    description: 'Bacterial spot causes small water-soaked spots on pepper leaves that turn into brown scabs, leading to heavy leaf drop and exposing peppers to sun damage.',
    etiology: {
      pathogenType: 'Bacterial Disease',
      incubationPeriod: '4 to 7 days',
      transmissionVectors: ['Water splash', 'Tools', 'Infected seeds'],
      inoculumSource: 'Infected seeds, old pepper stalks, and weed hosts.',
      hostInvasionMechanism: 'Enters leaf pores and tiny cuts during wet weather.',
    },
    symptoms: {
      leafMarkers: [
        'Small (1–3 mm) water-soaked spots turning dark brown.',
        'Spots have a light yellow-green halo.',
        'Leaves drop quickly, leaving bare stems.',
      ],
      canopyProgression: 'Spreads rapidly after heavy rains or overhead sprinkling.',
      stemAndFruitSigns: [
        'Rough scabby brown spots on pepper stems.',
        'Raised warty spots on green and red bell peppers.',
      ],
      lookAlikes: ['Bacterial Canker (causes wilted leaf margins)'],
    },
    microclimate: {
      temperatureRange: '24°C – 30°C',
      criticalHumidity: '> 80% RH',
      leafWetnessHours: '1–3 hours',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: ['Use certified disease-free pepper seeds and resistant hybrids.'],
      cropRotation: '2-year rotation with corn, beans, or cabbage.',
      airflowAndSpacing: 'Avoid overhead sprinklers; sanitize harvesting crates.',
      scoutingCadence: 'Twice-weekly checks on young growing shoots.',
      quarantineAction: 'Remove severely diseased plants; wash hands and shears.',
    },
  },

  // 8. Grape Black Rot
  'Grape___Black_rot': {
    name: 'Grape___Black_rot',
    displayName: 'Grapevine Black Rot',
    scientificName: 'Guignardia bidwellii',
    crop: 'Grape',
    category: 'Fungal',
    severity: 'High',
    description: 'Black rot attacks grape leaves, shoots, and fruit bunches. Infected grapes turn brown, shrivel up into hard, black wrinkled "mummies", and ruin the grape cluster.',
    etiology: {
      pathogenType: 'Foliar & Fruit Fungus',
      incubationPeriod: '7 to 14 days',
      transmissionVectors: ['Rain-splashed spores', 'Wind currents'],
      inoculumSource: 'Old dried mummified grapes left on the vine from last season.',
      hostInvasionMechanism: 'Spores penetrate young tender leaves and baby grapes.',
    },
    symptoms: {
      leafMarkers: [
        'Small reddish-brown circular spots with dark borders on leaves.',
        'Tiny black dots (spore sacs) inside the leaf spots.',
        'Brown dead patches near leaf veins.',
      ],
      canopyProgression: 'Starts on spring leaves and moves to grape bunches after blooming.',
      stemAndFruitSigns: [
        'Black oval sores on young vine shoots.',
        'Grapes turn brown, soften, rapidly turn black, and shrivel into hard mummies.',
      ],
      lookAlikes: ['Anthracnose (Bird’s eye rot with sunken grey centers and purple rims)'],
    },
    microclimate: {
      temperatureRange: '20°C – 27°C',
      criticalHumidity: '> 75% RH',
      leafWetnessHours: '6–10 hours of moisture',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Prune off and destroy all old shriveled black grape mummies in winter.',
        'Tie and train vines so bunches get plenty of sunlight and wind.',
        'Burn or deeply bury all pruned grape wood.',
      ],
      cropRotation: 'Permanent vineyard — maintain open canopy and winter pruning.',
      airflowAndSpacing: 'Thin out extra leafy shoots around grape bunches.',
      scoutingCadence: 'Weekly checks from flowering until harvest.',
      quarantineAction: 'Cut out infected vine shoots and remove fallen grape mummies.',
    },
  },

  // 9. Strawberry Leaf Scorch
  'Strawberry___Leaf_scorch': {
    name: 'Strawberry___Leaf_scorch',
    displayName: 'Strawberry Leaf Scorch',
    scientificName: 'Diplocarpon earlianum',
    crop: 'Strawberry',
    category: 'Fungal',
    severity: 'Moderate',
    description: 'Leaf Scorch produces small purple spots that spread across strawberry leaves, making them look scorched or burnt. This weakens the plants and reduces strawberry harvests.',
    etiology: {
      pathogenType: 'Foliar Fungus',
      incubationPeriod: '6 to 10 days',
      transmissionVectors: ['Splashing water', 'Wind-blown spores', 'Handling runners'],
      inoculumSource: 'Lives on old green strawberry leaves and plant crowns.',
      hostInvasionMechanism: 'Spores land on wet leaves and penetrate leaf skin directly.',
    },
    symptoms: {
      leafMarkers: [
        'Numerous small purple to dark brown spots across leaves.',
        'Spots do NOT have white centers (unlike common leaf spot).',
        'Leaf edges curl up and look scorched or burnt by fire.',
      ],
      canopyProgression: 'Spreads across the strawberry bed, slowing plant growth.',
      stemAndFruitSigns: ['Purple streaks on strawberry fruit stems making berries dry up.'],
      lookAlikes: ['Common Leaf Spot (has distinctive white centers inside spots)'],
    },
    microclimate: {
      temperatureRange: '18°C – 25°C',
      criticalHumidity: '> 80% RH',
      leafWetnessHours: '8–12 hours of leaf wetness',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: ['Cut away and discard scorched leaves when cleaning strawberry beds.'],
      cropRotation: 'Plant certified clean runner plants.',
      airflowAndSpacing: 'Use raised beds or table-top gutters so water drains quickly.',
      scoutingCadence: 'Check new strawberry leaves every 2 weeks.',
      quarantineAction: 'Trim off heavily scorched leaves and clean bed gutters.',
    },
  },

  // 10. Chilli Leaf Curl
  'Chilli___Leaf_curl': {
    name: 'Chilli___Leaf_curl',
    displayName: 'Chilli Leaf Curl Virus',
    scientificName: 'Chilli leaf curl virus (ChiLCV)',
    crop: 'Chillie',
    category: 'Viral',
    severity: 'High',
    description: 'Chilli Leaf Curl is a destructive viral disease transmitted by whiteflies. It causes upward curling, puckering, reduced leaf size, shortening of internodes, and severe stunting with poor fruit set.',
    etiology: {
      pathogenType: 'Begomovirus (Viral Disease)',
      incubationPeriod: '10 to 15 days following whitefly transmission',
      transmissionVectors: ['Whiteflies (Bemisia tabaci)', 'Infected nursery seedlings'],
      inoculumSource: 'Alternative weed hosts and infected nearby Solanaceae crops.',
      hostInvasionMechanism: 'Whiteflies feed on plant sap and introduce viral particles into phloem tissue.',
    },
    symptoms: {
      leafMarkers: [
        'Upward curling and severe puckering / crinkling of young foliage.',
        'Thickened veins with reduced leaf lamina size.',
        'Yellowing between veins on curled leaves.',
      ],
      canopyProgression: 'Begins on apical growing points and produces bushy, stunted dwarf plants.',
      stemAndFruitSigns: [
        'Shortened internodes causing crowded cluster appearance.',
        'Deformed, small, curled chilli pods with low market value.',
      ],
      lookAlikes: ['Thrips or broad mite feeding injury', 'Hormone herbicide drift damage'],
    },
    microclimate: {
      temperatureRange: '26°C – 35°C (Warm conditions promoting whitefly populations)',
      criticalHumidity: '< 65% RH (Dry, hot greenhouse conditions)',
      leafWetnessHours: '0–2 hours',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Install 50-mesh insect exclusion netting on all greenhouse vents and doorways.',
        'Hang yellow sticky traps (1 trap per 20–25 m²) to monitor and capture whiteflies.',
        'Immediately uproot and incinerate severely stunted virus-infected plants.',
      ],
      cropRotation: 'Avoid continuous planting of chillies, capsicums, or tomatoes in adjacent bays.',
      airflowAndSpacing: 'Maintain good weed management around greenhouse perimeters.',
      scoutingCadence: 'Inspect undersides of apical leaves twice weekly for whitefly nymphs.',
      quarantineAction: 'Bag and remove infected plants from the greenhouse immediately.',
    },
  },

  // 11. Chilli Anthracnose
  'Chilli___Anthracnose': {
    name: 'Chilli___Anthracnose',
    displayName: 'Chilli Anthracnose (Fruit Rot / Dieback)',
    scientificName: 'Colletotrichum capsici',
    crop: 'Chillie',
    category: 'Fungal',
    severity: 'High',
    description: 'Anthracnose attacks both foliage and ripening chilli pods. It causes circular sunken water-soaked lesions with concentric rings of dark acervuli, leading to fruit rotting and twig dieback.',
    etiology: {
      pathogenType: 'Necrotrophic Foliar & Fruit Fungus',
      incubationPeriod: '3 to 7 days in warm humid conditions',
      transmissionVectors: ['Splashing water', 'Contaminated seeds', 'Wind-driven rain'],
      inoculumSource: 'Infected plant debris, seed-borne mycelium, and alternate hosts.',
      hostInvasionMechanism: 'Spores germinate on wet surfaces, producing appressoria to pierce cuticle.',
    },
    symptoms: {
      leafMarkers: [
        'Small, circular brown spots that dry out and form shot-holes.',
        'Dieback of apical twigs turning straw-colored from top down.',
      ],
      canopyProgression: 'Spreads rapidly during overhead irrigation or high humidity periods.',
      stemAndFruitSigns: [
        'Sunken circular or elliptical spots on ripening green and red chilli fruits.',
        'Concentric rings of salmon-pink to black fungal spore masses on fruit lesions.',
      ],
      lookAlikes: ['Sunscald on exposed fruit', 'Bacterial spot fruit lesions'],
    },
    microclimate: {
      temperatureRange: '25°C – 30°C',
      criticalHumidity: '> 85% RH',
      leafWetnessHours: '4–8 hours of fruit wetness',
      vpdRiskLevel: 'High',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Collect and destroy all diseased chilli fruits and fallen twigs.',
        'Use certified disease-free seeds and apply bio-fungicide seed treatments.',
        'Switch from overhead spray to drip irrigation to keep pods dry.',
      ],
      cropRotation: 'Rotate with non-solanaceous crops such as maize, beans, or brassicas for 2 years.',
      airflowAndSpacing: 'Ensure proper plant spacing (45–60 cm) to allow sunlight penetration.',
      scoutingCadence: 'Scout developing pods weekly, especially as fruits start color turning.',
      quarantineAction: 'Discard all blemished pods before harvest packaging.',
    },
  },

  // 12. Healthy Crop
  'Healthy': {
    name: 'Healthy',
    displayName: 'Healthy Crop (No Disease Detected)',
    scientificName: 'Vibrant, Normal Foliage',
    crop: 'Greenhouse Crop',
    category: 'Healthy',
    severity: 'Healthy',
    description: 'Your crop leaf is completely healthy! It has vibrant green color, strong leaf structure, and no signs of disease or pests. Keep up the great greenhouse care!',
    etiology: {
      pathogenType: 'None (Healthy Crop)',
      incubationPeriod: 'N/A',
      transmissionVectors: ['None'],
      inoculumSource: 'None (Clean greenhouse management)',
      hostInvasionMechanism: 'Natural plant immunity is strong and active.',
    },
    symptoms: {
      leafMarkers: [
        'Vibrant, deep green leaf color with clean smooth surface.',
        'No brown spots, yellow halos, or white mold.',
        'Strong leaf stems and healthy normal veins.',
      ],
      canopyProgression: 'Even, steady growth throughout the greenhouse.',
      stemAndFruitSigns: ['Healthy firm green stems and clean developing fruit.'],
      lookAlikes: ['None'],
    },
    microclimate: {
      temperatureRange: '20°C – 26°C (Ideal Greenhouse Climate)',
      criticalHumidity: '60% – 70% RH (Safe Green Zone)',
      leafWetnessHours: '0 hours (Dry Leaves)',
      vpdRiskLevel: 'Low',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Continue regular clean greenhouse habits and shoe footbaths.',
        'Keep up balanced fertilizer feeding and watering.',
        'Maintain daily temperature and humidity checks.',
      ],
      cropRotation: 'Follow standard seasonal crop plans.',
      airflowAndSpacing: 'Keep fans running to maintain steady gentle air movement.',
      scoutingCadence: 'Do routine weekly walk-throughs across all greenhouse bays.',
      quarantineAction: 'No quarantine needed; your crop is in great health!',
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

  const cleanName = diseaseName.replace(/___/g, ' - ').replace(/_/g, ' ');
  const isBacterial = diseaseName.toLowerCase().includes('bacteri');
  const isViral = diseaseName.toLowerCase().includes('virus');
  const isPest = diseaseName.toLowerCase().includes('mite') || diseaseName.toLowerCase().includes('pest');

  return {
    name: diseaseName,
    displayName: cleanName,
    scientificName: isBacterial ? 'Bacterial Crop Infection' : isViral ? 'Plant Virus' : isPest ? 'Pest Vector' : 'Fungal Leaf Spot',
    crop: 'Greenhouse Crop',
    category: isBacterial ? 'Bacterial' : isViral ? 'Viral' : isPest ? 'Pest' : 'Fungal',
    severity: isViral || isBacterial ? 'High' : 'Moderate',
    description: `AI disease scan identified markers of ${cleanName}. Early action is recommended to protect surrounding plants.`,
    etiology: {
      pathogenType: isBacterial ? 'Bacterial Infection' : isViral ? 'Plant Virus' : 'Foliar Fungus',
      incubationPeriod: '4 to 8 days',
      transmissionVectors: ['Water splash', 'Air currents', 'Dirty tools'],
      inoculumSource: 'Old crop leaves and greenhouse soil.',
      hostInvasionMechanism: 'Enters leaf pores or small cuts during humid weather.',
    },
    symptoms: {
      leafMarkers: [
        'Discolored leaves with brown spots or yellowing.',
        'Irregular patches or curled leaf margins.',
      ],
      canopyProgression: 'Spreads to nearby plants under humid greenhouse conditions.',
      stemAndFruitSigns: ['Possible marks on stems or fruit.'],
      lookAlikes: ['Nutrient deficiency or dry weather stress'],
    },
    microclimate: {
      temperatureRange: '20°C – 28°C',
      criticalHumidity: '> 75% RH',
      leafWetnessHours: '2–4 hours',
      vpdRiskLevel: 'Moderate',
    },
    preventionAndQuarantine: {
      sanitation: [
        'Prune affected leaves with clean shears.',
        'Keep greenhouse clean and sweep up fallen leaves.',
        'Avoid handling plants when leaves are wet.',
      ],
      cropRotation: 'Rotate with different crop varieties each season.',
      airflowAndSpacing: 'Ensure good plant spacing and run ventilation fans.',
      scoutingCadence: 'Inspect crops twice a week.',
      quarantineAction: 'Isolate affected plants and check neighboring rows.',
    },
  };
};
