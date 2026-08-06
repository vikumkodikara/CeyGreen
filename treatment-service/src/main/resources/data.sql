-- Seed data for treatments. Runs on startup when sql.init.mode=always.
-- Uses INSERT ... ON CONFLICT to be idempotent.

-- Diseases
INSERT INTO diseases (id, name, description) VALUES
    (1, 'Leaf Blight', 'Fungal infection causing brown spots and leaf death'),
    (2, 'Powdery Mildew', 'White powdery coating on leaves caused by fungi'),
    (3, 'Root Rot', 'Fungal disease affecting roots due to overwatering'),
    (4, 'Aphid Infestation', 'Sap-sucking insects causing curled and yellowed leaves'),
    (5, 'Bacterial Wilt', 'Bacterial infection causing wilting without yellowing')
ON CONFLICT (id) DO NOTHING;

-- Treatments
INSERT INTO treatments (id, disease_id, product_name, type, dosage, frequency, safety_notes, active) VALUES
    (1, 1, 'Mancozeb 75% WP', 'CHEMICAL', '2g per litre of water', 'Every 7 days', 'Wear protective equipment. Do not apply within 14 days of harvest.', true),
    (2, 1, 'Neem Oil Extract', 'ORGANIC', '5ml per litre of water', 'Every 5 days', 'Safe for organic farming. Apply in evening to avoid leaf burn.', true),
    (3, 2, 'Sulphur 80% WDG', 'CHEMICAL', '3g per litre of water', 'Every 10 days', 'Do not mix with oil-based sprays. Avoid application above 35°C.', true),
    (4, 2, 'Baking Soda Solution', 'ORGANIC', '1 tablespoon per litre with liquid soap', 'Every 7 days', 'Test on a small area first.', true),
    (5, 3, 'Metalaxyl 35% WS', 'CHEMICAL', 'Seed treatment at 2g per kg', 'At planting', 'Handle with care. Store away from food.', true),
    (6, 3, 'Trichoderma viride', 'ORGANIC', '5g per litre of water for soil drench', 'Every 14 days', 'Apply to moist soil. Compatible with organic certification.', true),
    (7, 4, 'Imidacloprid 17.8% SL', 'CHEMICAL', '0.5ml per litre of water', 'Once, at first sign', 'Toxic to bees. Do not spray during flowering.', true),
    (8, 4, 'Ladybird Beetle Release', 'ORGANIC', '500 beetles per 50m²', 'Once per outbreak', 'Release in evening. Avoid pesticide use for 2 weeks before.', true),
    (9, 5, 'Copper Hydroxide', 'CHEMICAL', '2g per litre of water', 'Every 7 days', 'Pre-harvest interval: 7 days. Avoid over-application.', true),
    (10, 5, 'Crop Rotation', 'ORGANIC', 'Rotate to non-host crop for 2-3 seasons', 'Per growing season', 'Most effective long-term prevention strategy.', true)
ON CONFLICT (id) DO NOTHING;
