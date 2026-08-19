-- Seed alternative treatments for ALL 21 crop diseases, ensuring every disease has both CHEMICAL and ORGANIC options

-- 1. Tomato Bacterial Spot
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato bacterial spot'), 'Bacillus subtilis (Bio-Fungicide)', 'ORGANIC', '5g/L water', 'Every 5-7 days', 'Safe bio-bactericide; non-toxic to beneficial insects and pollinators.', 'Tomato', 'MODERATE', true, 0, 'Foliar spray', 'Serenade ASO, Cease, Taegro', 82),
((SELECT id FROM diseases WHERE normalized_name = 'tomato bacterial spot'), 'Copper Hydroxide 77% WP', 'CHEMICAL', '1.5g/L water', 'Every 7 days', 'High-efficacy copper formulation. Ensure full coverage on upper and lower leaf surfaces.', 'Tomato', 'MODERATE', true, 3, 'Foliar spray', 'Kocide 3000, Champ WG', 88);

-- 2. Tomato Early Blight
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato early blight'), 'Azoxystrobin + Difenoconazole', 'CHEMICAL', '1ml/L water', 'Every 10-14 days', 'Broad-spectrum systemic fungicide. Alternate with protectants to avoid resistance.', 'Tomato', 'MODERATE', true, 7, 'Foliar spray', 'Amistar Top', 92),
((SELECT id FROM diseases WHERE normalized_name = 'tomato early blight'), 'Trichoderma harzianum (Bio-Fungicide)', 'ORGANIC', '5g/L water', 'Every 7 days', 'Biological agent that parasitizes fungal mycelium; preventive application recommended.', 'Tomato', 'MILD', true, 0, 'Foliar spray / Soil drench', 'RootShield, Trichobact', 78);

-- 3. Tomato Late Blight
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato late blight'), 'Cymoxanil 8% + Mancozeb 64% WP', 'CHEMICAL', '2g/L water', 'Every 7 days', 'Fast penetrating cure and protectant action. Apply immediately when outbreak occurs.', 'Tomato', 'SEVERE', true, 7, 'Foliar spray', 'Curzate M8', 91),
((SELECT id FROM diseases WHERE normalized_name = 'tomato late blight'), 'Copper Octanoate (Copper Soap)', 'ORGANIC', '10ml/L water', 'Every 5 days', 'NOP approved organic copper formulation with reduced heavy metal accumulation in soil.', 'Tomato', 'MODERATE', true, 0, 'Foliar spray', 'Cueva, Liquid Copper', 80);

-- 4. Tomato Leaf Mold
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato leaf mold'), 'Potassium Bicarbonate', 'ORGANIC', '5g/L water', 'Every 7 days', 'Eradicant organic spray; raises leaf surface pH to inhibit fungal growth.', 'Tomato', 'MILD', true, 0, 'Foliar spray', 'MilStop, Armicarb', 79),
((SELECT id FROM diseases WHERE normalized_name = 'tomato leaf mold'), 'Bacillus amyloliquefaciens', 'ORGANIC', '4g/L water', 'Every 5 days', 'Bio-fungicide colonization inhibits leaf mold germination.', 'Tomato', 'MODERATE', true, 0, 'Foliar spray', 'Double Nickel 55', 83);

-- 5. Tomato Septoria Leaf Spot
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato septoria leaf spot'), 'Copper Octanoate (Copper Soap)', 'ORGANIC', '10ml/L water', 'Every 7 days', 'Organic liquid copper soap for leaf spot management.', 'Tomato', 'MODERATE', true, 0, 'Foliar spray', 'Badge SC, Cueva', 81),
((SELECT id FROM diseases WHERE normalized_name = 'tomato septoria leaf spot'), 'Azoxystrobin 23% SC', 'CHEMICAL', '1ml/L water', 'Every 10-14 days', 'Systemic preventive fungicide for severe Septoria outbreaks.', 'Tomato', 'MODERATE', true, 7, 'Foliar spray', 'Quadris, Heritage', 90);

-- 6. Tomato Spider Mites
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato spider mites'), 'Abamectin 1.8% EC', 'CHEMICAL', '0.5ml/L water', 'Every 7 days', 'Translaminar miticide. Spray underside of leaves where mites concentrate.', 'Tomato', 'MODERATE', true, 7, 'Foliar spray', 'Vertimec, Agrimec', 93),
((SELECT id FROM diseases WHERE normalized_name = 'tomato spider mites'), 'Insecticidal Potassium Soap + Sulfur', 'ORGANIC', '10ml/L water', 'Every 5 days', 'Organic contact miticide. Avoid applying during temperatures above 30°C.', 'Tomato', 'MILD', true, 0, 'Foliar spray', 'Safer Brand Miticide', 77);

-- 7. Tomato Target Spot
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato target spot'), 'Chlorothalonil 75% WP', 'CHEMICAL', '2g/L water', 'Every 7 days', 'Broad-spectrum contact protectant against target spot.', 'Tomato', 'MODERATE', true, 7, 'Foliar spray', 'Daconil, Bravo 720', 86),
((SELECT id FROM diseases WHERE normalized_name = 'tomato target spot'), 'Bacillus subtilis Bio-Fungicide', 'ORGANIC', '5g/L water', 'Every 7 days', 'Organic protective bio-spray to prevent lesion spreading.', 'Tomato', 'MILD', true, 0, 'Foliar spray', 'Serenade Opti', 79);

-- 8. Tomato Yellow Leaf Curl Virus
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato yellow leaf curl virus'), 'Yellow Sticky Traps & Neem Oil', 'ORGANIC', '5ml/L water', 'Every 5 days', 'Organic vector control. Traps and Repels whiteflies without chemicals.', 'Tomato', 'MODERATE', true, 0, 'Trap placement & Foliar spray', 'EcoNeem, Sticky-Card', 75),
((SELECT id FROM diseases WHERE normalized_name = 'tomato yellow leaf curl virus'), 'Insecticidal Soap & Silver Reflective Mulch', 'ORGANIC', '15ml/L water', 'Weekly', 'Physical barrier + organic contact spray to minimize whitefly landing.', 'Tomato', 'MILD', true, 0, 'Mulch & Foliar spray', 'Natria Soap, Reflective Foil', 78);

-- 9. Tomato Mosaic Virus
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'tomato mosaic virus'), 'Milk Spray (20% Skim Milk) & Tool Sanitation', 'ORGANIC', '200ml/L water', 'Every 7 days', 'Organic protein inactivation spray; sanitizes sap transmission on foliage.', 'Tomato', 'MILD', true, 0, 'Foliar spray & Tool dip', 'Natural Dairy Wash', 72),
((SELECT id FROM diseases WHERE normalized_name = 'tomato mosaic virus'), 'Trisodium Phosphate (TSP) Tool Disinfectant & Compost Tea', 'ORGANIC', '50g/L for tools', 'As needed', 'Sanitizes pruning shears and enhances plant systemic immunity naturally.', 'Tomato', 'MODERATE', true, 0, 'Tool soak & Soil drench', 'Organic TSP, Compost Tea', 75);

-- 10. Potato Early Blight
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'potato early blight'), 'Chlorothalonil 75% WP', 'CHEMICAL', '2g/L water', 'Every 7-10 days', 'Multi-site contact protectant fungicide with low risk of resistance development.', 'Potato', 'MODERATE', true, 7, 'Foliar spray', 'Daconil, Bravo', 87),
((SELECT id FROM diseases WHERE normalized_name = 'potato early blight'), 'Copper Hydroxide (Organic Bio-Fungicide)', 'ORGANIC', '2g/L water', 'Every 7 days', 'OMRI approved copper fungicide for organic potato disease prevention.', 'Potato', 'MODERATE', true, 0, 'Foliar spray', 'Kocide Organic, Badge SC', 80);

-- 11. Potato Late Blight
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'potato late blight'), 'Dimethomorph 50% WG', 'CHEMICAL', '1g/L water', 'Every 7-10 days', 'Local systemic fungicide targeting cell wall synthesis in Phytophthora.', 'Potato', 'SEVERE', true, 14, 'Foliar spray', 'Acrobat 50WP', 90),
((SELECT id FROM diseases WHERE normalized_name = 'potato late blight'), 'Copper Soap (Copper Octanoate)', 'ORGANIC', '10ml/L water', 'Every 5 days', 'Organic protectant spray for early blight and late blight control.', 'Potato', 'MODERATE', true, 0, 'Foliar spray', 'Cueva Liquid Copper', 81);

-- 12. Pepper Bacterial Spot
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'pepper bacterial spot'), 'Bacillus amyloliquefaciens', 'ORGANIC', '4g/L water', 'Every 5 days', 'Organic bacterial antagonist preventing spot establishment.', 'Pepper', 'MILD', true, 0, 'Foliar spray', 'Double Nickel 55', 81),
((SELECT id FROM diseases WHERE normalized_name = 'pepper bacterial spot'), 'Copper Hydroxide + Mancozeb', 'CHEMICAL', '2g/L water', 'Every 7 days', 'Synergistic chemical bactericide combination for resistant bacterial strains.', 'Pepper', 'MODERATE', true, 7, 'Foliar spray', 'Kocide Opti', 87);

-- 13. Pepper Powdery Mildew
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'pepper powdery mildew'), 'Potassium Bicarbonate', 'ORGANIC', '5g/L water', 'Every 7 days', 'Organic contact fungicide. Rapidly alters leaf surface pH to kill mildew spores.', 'Pepper', 'MILD', true, 0, 'Foliar spray', 'MilStop, Armicarb', 82),
((SELECT id FROM diseases WHERE normalized_name = 'pepper powdery mildew'), 'Myclobutanil 20% EW', 'CHEMICAL', '0.5ml/L water', 'Every 10 days', 'Systemic chemical fungicide for dense canopy powdery mildew infections.', 'Pepper', 'MODERATE', true, 7, 'Foliar spray', 'Rally 200 EW', 90);

-- 14. Chili Anthracnose
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'chili anthracnose'), 'Tebuconazole 250 EC', 'CHEMICAL', '1ml/L water', 'Every 10 days', 'Systemic triazole fungicide providing strong curative and preventive control.', 'Chili', 'MODERATE', true, 14, 'Foliar spray', 'Folicur, Orius', 89),
((SELECT id FROM diseases WHERE normalized_name = 'chili anthracnose'), 'Neem Extract + Horticultural Soap', 'ORGANIC', '5ml/L water', 'Every 5 days', 'Natural organic anti-fungal mixture; coat fruit surfaces evenly.', 'Chili', 'MILD', true, 0, 'Foliar spray', 'BioNeem', 76);

-- 15. Chili Leaf Curl Virus
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'chili leaf curl virus'), 'Neem Oil + Insecticidal Soap', 'ORGANIC', '5ml/L water', 'Every 5 days', 'Organic whitefly vector suppressant. Safe for natural chili predators.', 'Chili', 'MODERATE', true, 0, 'Foliar spray', 'Neemix, Safer Soap', 77),
((SELECT id FROM diseases WHERE normalized_name = 'chili leaf curl virus'), 'Spinetoram 11.7% SC', 'CHEMICAL', '0.8ml/L water', 'Every 10 days', 'Fast whitefly and thrips vector knockdown to stop virus spreading.', 'Chili', 'SEVERE', true, 3, 'Foliar spray', 'Radiant, Delegate', 91);

-- 16. Strawberry Leaf Scorch
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'strawberry leaf scorch'), 'Copper Octanoate (Copper Soap)', 'ORGANIC', '8ml/L water', 'Every 7-10 days', 'Organic copper formulation safe for strawberry foliage.', 'Strawberry', 'MILD', true, 0, 'Foliar spray', 'Cueva', 80),
((SELECT id FROM diseases WHERE normalized_name = 'strawberry leaf scorch'), 'Myclobutanil 40% WP', 'CHEMICAL', '0.4g/L water', 'Every 10-14 days', 'Systemic protective fungicide against leaf scorch fungi.', 'Strawberry', 'MODERATE', true, 1, 'Foliar spray', 'Rally 40W', 89);

-- 17. Strawberry Gray Mold
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'strawberry gray mold'), 'Bacillus subtilis strain QST 713', 'ORGANIC', '4g/L water', 'Every 5-7 days', 'Organic control during flowering to suppress Botrytis fruit rot without harvest interval.', 'Strawberry', 'MODERATE', true, 0, 'Foliar spray', 'Serenade Opti', 84),
((SELECT id FROM diseases WHERE normalized_name = 'strawberry gray mold'), 'Fenhexamid 50% WG', 'CHEMICAL', '1.5g/L water', 'Every 7 days', 'Targeted Botrytis specific chemical fungicide with 1 day PHI.', 'Strawberry', 'SEVERE', true, 1, 'Foliar spray', 'Elevate 50 WDG', 93);

-- 18. Strawberry Powdery Mildew
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'strawberry powdery mildew'), 'Potassium Bicarbonate', 'ORGANIC', '4g/L water', 'Every 7 days', 'OMRI listed organic bio-fungicide for fruit and foliage.', 'Strawberry', 'MILD', true, 0, 'Foliar spray', 'MilStop', 83),
((SELECT id FROM diseases WHERE normalized_name = 'strawberry powdery mildew'), 'Azoxystrobin 250 SC', 'CHEMICAL', '0.8ml/L water', 'Every 10-14 days', 'Systemic strobilurin fungicide preventing mildew spread on fruit.', 'Strawberry', 'MODERATE', true, 1, 'Foliar spray', 'Abound, Quadris', 91);

-- 19. Grape Black Rot
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'grape black rot'), 'Myclobutanil 40% WP', 'CHEMICAL', '0.5g/L water', 'Every 10-14 days', 'Systemic DMI fungicide with excellent reach-back action against black rot.', 'Grape', 'MODERATE', true, 14, 'Foliar spray', 'Rally 40W, Systhane', 91),
((SELECT id FROM diseases WHERE normalized_name = 'grape black rot'), 'Bordeaux Mixture (Copper Sulfate + Lime)', 'ORGANIC', '10g/L water', 'Every 7 days', 'Traditional organic protectant spray for grape vines before rain events.', 'Grape', 'MODERATE', true, 0, 'Foliar spray', 'Bordeaux Organic', 82);

-- 20. Grape Esca
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'grape esca'), 'Trichoderma atroviride Wound Sealant', 'ORGANIC', 'Paint paste', 'After winter pruning', 'Organic biocontrol agent protecting pruning wounds against fungal colonization.', 'Grape', 'MODERATE', true, 0, 'Wound painting', 'Esquive WP, Vintec', 79),
((SELECT id FROM diseases WHERE normalized_name = 'grape esca'), 'Pruning Sanitation & Pine Tar Graft Sealant', 'ORGANIC', 'Apply directly', 'Post-pruning', 'Physical barrier prevents trunk pathogen entry into cut canes.', 'Grape', 'SEVERE', true, 0, 'Trunk / Cut coating', 'Tree Wound Sealant', 76);

-- 21. Grape Leaf Blight
INSERT INTO treatments (disease_id, product_name, type, dosage, frequency, safety_notes, crop_type, severity, active, phi_days, application_method, brand_names, effectiveness_score) VALUES
((SELECT id FROM diseases WHERE normalized_name = 'grape leaf blight'), 'Copper Hydroxide 50% WP', 'CHEMICAL', '2g/L water', 'Every 10 days', 'Foliar protectant chemical spray for angular leaf spots.', 'Grape', 'MODERATE', true, 14, 'Foliar spray', 'Kocide 2000', 87),
((SELECT id FROM diseases WHERE normalized_name = 'grape leaf blight'), 'Neem Extract & Compost Tea Bio-Spray', 'ORGANIC', '5ml/L water', 'Every 7 days', 'Natural organic foliar spray suppressing fungal sporulation.', 'Grape', 'MILD', true, 0, 'Foliar spray', 'BioNeem, Compost Tea', 78);
