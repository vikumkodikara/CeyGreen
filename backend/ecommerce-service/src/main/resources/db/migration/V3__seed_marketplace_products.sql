-- Demo marketplace listings aligned with CeyGreen supported greenhouse crops
-- (Tomato, Potato, Pepper / Bell Pepper, Strawberry, Grape)

UPDATE products
SET description = 'Greenhouse-grown Roma tomatoes (Solanum lycopersicum). Firm fruit, ideal for salads and curries.',
    created_at = COALESCE(created_at, NOW())
WHERE id = 1;

INSERT INTO products (farmer_id, crop_name, quantity, unit_price, harvest_date, location, active, description, created_at)
VALUES
    -- Tomato — Solanum lycopersicum
    ('22222222-2222-2222-2222-222222222222', 'Tomato', 35, 165.00, CURRENT_DATE - 1, 'Nuwara Eliya',
     TRUE, 'Cherry tomatoes (Solanum lycopersicum) from cool-climate greenhouse tunnels. Sweet and vine-ripened.', NOW() - INTERVAL '2 days'),
    ('44444444-4444-4444-4444-444444444444', 'Tomato', 28, 140.00, CURRENT_DATE - 2, 'Anuradhapura',
     TRUE, 'Beefsteak tomatoes (Solanum lycopersicum), hydroponic harvest. Large, juicy slicers.', NOW() - INTERVAL '1 day'),

    -- Potato — Solanum tuberosum
    ('33333333-3333-3333-3333-333333333333', 'Potato', 50, 95.00, CURRENT_DATE - 3, 'Badulla',
     TRUE, 'New potatoes (Solanum tuberosum), waxy texture. Perfect for boiling and salads.', NOW() - INTERVAL '3 days'),
    ('22222222-2222-2222-2222-222222222222', 'Potato', 60, 88.00, CURRENT_DATE - 1, 'Nuwara Eliya',
     TRUE, 'Highland potatoes (Solanum tuberosum) grown in raised beds. Clean skin, uniform size.', NOW() - INTERVAL '4 days'),
    ('11111111-1111-1111-1111-111111111111', 'Potato', 40, 102.00, CURRENT_DATE, 'Matale',
     TRUE, 'Organic table potatoes (Solanum tuberosum). Low pesticide, rich flavour.', NOW()),

    -- Pepper / Bell Pepper — Capsicum annuum
    ('33333333-3333-3333-3333-333333333333', 'Pepper / Bell Pepper', 22, 220.00, CURRENT_DATE - 1, 'Galle',
     TRUE, 'Mixed bell peppers (Capsicum annuum): red, yellow, and green. Crisp and thick-walled.', NOW() - INTERVAL '1 day'),
    ('44444444-4444-4444-4444-444444444444', 'Pepper / Bell Pepper', 18, 245.00, CURRENT_DATE, 'Jaffna',
     TRUE, 'Sweet bell peppers (Capsicum annuum) from climate-controlled greenhouse. Export quality.', NOW()),
    ('11111111-1111-1111-1111-111111111111', 'Pepper / Bell Pepper', 15, 210.00, CURRENT_DATE - 2, 'Kandy',
     TRUE, 'Green bell peppers (Capsicum annuum), hand-picked twice weekly. Ideal for stir-fry.', NOW() - INTERVAL '2 days'),

    -- Strawberry — Fragaria × ananassa
    ('22222222-2222-2222-2222-222222222222', 'Strawberry', 12, 450.00, CURRENT_DATE, 'Nuwara Eliya',
     TRUE, 'Fresh strawberries (Fragaria x ananassa). Sweet alpine variety, sold in 250 g punnets.', NOW()),
    ('11111111-1111-1111-1111-111111111111', 'Strawberry', 10, 420.00, CURRENT_DATE - 1, 'Kandy',
     TRUE, 'Day-neutral strawberries (Fragaria x ananassa). Bright colour, firm texture.', NOW() - INTERVAL '1 day'),
    ('33333333-3333-3333-3333-333333333333', 'Strawberry', 8, 480.00, CURRENT_DATE, 'Galle',
     TRUE, 'Premium greenhouse strawberries (Fragaria x ananassa). Picked same morning.', NOW()),

    -- Grape — Vitis vinifera
    ('44444444-4444-4444-4444-444444444444', 'Grape', 25, 380.00, CURRENT_DATE - 2, 'Jaffna',
     TRUE, 'Seedless table grapes (Vitis vinifera). Sweet clusters, chilled storage.', NOW() - INTERVAL '2 days'),
    ('33333333-3333-3333-3333-333333333333', 'Grape', 20, 395.00, CURRENT_DATE - 1, 'Galle',
     TRUE, 'Greenhouse grapes (Vitis vinifera), Thompson-style. Crisp skin, high Brix.', NOW() - INTERVAL '1 day'),
    ('22222222-2222-2222-2222-222222222222', 'Grape', 30, 360.00, CURRENT_DATE, 'Badulla',
     TRUE, 'Table grapes (Vitis vinifera) from trellised vines. Locally grown, no wax coating.', NOW());
