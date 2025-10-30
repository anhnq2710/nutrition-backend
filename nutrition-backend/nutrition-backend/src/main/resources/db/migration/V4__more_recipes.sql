-- V4__more_recipes_fixed.sql
-- 5 sample recipes — each block self-contained

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ========== 1. Oatmeal Breakfast Bowl ==========
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
),
     r_ins AS (
         INSERT INTO recipes (id, user_id, name, servings, created_at)
             SELECT gen_random_uuid(), du.user_id, 'Oatmeal Breakfast Bowl', 1, now()
             FROM demo_user du
             WHERE NOT EXISTS (
                 SELECT 1 FROM recipes r WHERE r.name = 'Oatmeal Breakfast Bowl' AND r.user_id = du.user_id
             )
             RETURNING id
     )
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.id, fi.id, 100, 'g', 'Cooked with water'
FROM r_ins r
         JOIN food_items fi ON fi.name = 'Brown Rice (Cooked)'
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.food_item_id = fi.id
);

-- ========== 2. Grilled Chicken Salad ==========
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
),
     r_ins AS (
         INSERT INTO recipes (id, user_id, name, servings, created_at)
             SELECT gen_random_uuid(), du.user_id, 'Grilled Chicken Salad', 1, now()
             FROM demo_user du
             WHERE NOT EXISTS (
                 SELECT 1 FROM recipes r WHERE r.name = 'Grilled Chicken Salad' AND r.user_id = du.user_id
             )
             RETURNING id
     )
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.id, fi.id, 120, 'g', 'Grilled chicken sliced for salad'
FROM r_ins r
         JOIN food_items fi ON fi.name = 'Chicken Breast (Grilled)'
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.food_item_id = fi.id
);

-- ========== 3. Broccoli & Rice Stir-Fry ==========
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
),
     r_ins AS (
         INSERT INTO recipes (id, user_id, name, servings, created_at)
             SELECT gen_random_uuid(), du.user_id, 'Broccoli & Rice Stir-Fry', 1, now()
             FROM demo_user du
             WHERE NOT EXISTS (
                 SELECT 1 FROM recipes r WHERE r.name = 'Broccoli & Rice Stir-Fry' AND r.user_id = du.user_id
             )
             RETURNING id
     )
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.id, fi.id, 100, 'g', 'Stir-fried with olive oil'
FROM r_ins r
         JOIN food_items fi ON fi.name = 'Broccoli (Steamed)'
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.food_item_id = fi.id
);

-- ========== 4. Banana Energy Snack ==========
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
),
     r_ins AS (
         INSERT INTO recipes (id, user_id, name, servings, created_at)
             SELECT gen_random_uuid(), du.user_id, 'Banana Energy Snack', 1, now()
             FROM demo_user du
             WHERE NOT EXISTS (
                 SELECT 1 FROM recipes r WHERE r.name = 'Banana Energy Snack' AND r.user_id = du.user_id
             )
             RETURNING id
     )
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.id, fi.id, 1, 'medium', 'Quick snack before workout'
FROM r_ins r
         JOIN food_items fi ON fi.name = 'Banana'
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.food_item_id = fi.id
);

-- ========== 5. Olive Oil Dressing ==========
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
),
     r_ins AS (
         INSERT INTO recipes (id, user_id, name, servings, created_at)
             SELECT gen_random_uuid(), du.user_id, 'Olive Oil Dressing', 1, now()
             FROM demo_user du
             WHERE NOT EXISTS (
                 SELECT 1 FROM recipes r WHERE r.name = 'Olive Oil Dressing' AND r.user_id = du.user_id
             )
             RETURNING id
     )
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.id, fi.id, 10, 'ml', 'For salads or pasta'
FROM r_ins r
         JOIN food_items fi ON fi.name = 'Olive Oil'
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id AND ri.food_item_id = fi.id
);
