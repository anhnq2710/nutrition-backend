-- V5__add_food_items.sql
-- Add additional food items: Oats, Eggs, Milk (whole), Tuna (canned in water), Sweet Potato (baked)
-- Idempotent: each insert only runs if a food with same name does not already exist.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Oats (rolled oats) per 100 g ~ 389 kcal
INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                        calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
SELECT gen_random_uuid(), 'Oats (Rolled)', 'Generic', '1/2 cup (40 g)', 40, 'g',
       (389.0 * 40 / 100), (16.9 * 40 / 100), (66.3 * 40 / 100), (6.9 * 40 / 100),
       389.0, 'manual', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM food_items fi WHERE fi.name = 'Oats (Rolled)');

-- Eggs (large, boiled) per 1 large ~ 78 kcal
INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                        calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
SELECT gen_random_uuid(), 'Egg (Boiled, large)', 'Generic', '1 large (50 g)', 50, 'g',
       78.0, 6.3, 0.6, 5.3, (155.0), 'manual', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM food_items fi WHERE fi.name = 'Egg (Boiled, large)');

-- Milk (whole) per 1 cup (244 g) ~ 149 kcal
INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                        calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
SELECT gen_random_uuid(), 'Milk (Whole)', 'Generic', '1 cup (244 g)', 244, 'g',
       149.0, 7.7, 11.7, 8.0, (61.0), 'manual', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM food_items fi WHERE fi.name = 'Milk (Whole)');

-- Tuna (canned in water), per 100 g ~ 116 kcal
INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                        calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
SELECT gen_random_uuid(), 'Tuna (Canned in water)', 'Generic', '1 can (100 g drained)', 100, 'g',
       116.0, 26.0, 0.0, 0.8, 116.0, 'manual', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM food_items fi WHERE fi.name = 'Tuna (Canned in water)');

-- Sweet Potato (baked) per 100 g ~ 90 kcal (1 medium ~130 g => ~117 kcal)
INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                        calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
SELECT gen_random_uuid(), 'Sweet Potato (Baked)', 'Generic', '1 medium (130 g)', 130, 'g',
       (90.0 * 130 / 100), (1.6 * 130 / 100), (20.1 * 130 / 100), (0.1 * 130 / 100),
       90.0, 'manual', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM food_items fi WHERE fi.name = 'Sweet Potato (Baked)');
