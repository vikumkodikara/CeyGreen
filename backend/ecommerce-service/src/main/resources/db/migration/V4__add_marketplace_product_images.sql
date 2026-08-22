-- Product photos served from the React client public folder (client/public/marketplace/).

UPDATE products SET image_url = '/marketplace/tomato.webp'
WHERE crop_name = 'Tomato';

UPDATE products SET image_url = '/marketplace/bell-pepper.webp'
WHERE crop_name = 'Pepper / Bell Pepper';

UPDATE products SET image_url = '/marketplace/strawberry.webp'
WHERE crop_name = 'Strawberry';

UPDATE products SET image_url = '/marketplace/grapes.webp'
WHERE crop_name = 'Grape';

-- Potato image is added in V5__add_potato_marketplace_image.sql
