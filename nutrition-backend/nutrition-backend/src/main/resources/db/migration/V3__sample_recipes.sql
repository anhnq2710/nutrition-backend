-- V3__sample_recipes.sql
-- Seed sample recipes and ingredients (idempotent)
-- Requires food_items and users seeded already (from V1/V2)

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Find demo user id (if exists)
WITH demo_user AS (
    SELECT id AS user_id FROM users WHERE email = 'demo@nutritionapp.com' LIMIT 1
    ),

-- -------------------------
-- Recipe 1: Chicken Rice Bowl
-- -------------------------
-- Insert recipe if not exists (by name + user)
    recipe1 AS (
SELECT user_id FROM demo_user
    ),
    ins_recipe1 AS (
INSERT INTO recipes (id, user_id, name, servings, created_at)
SELECT gen_random_uuid(), r.user_id, 'Chicken Rice Bowl', 1, now()
FROM recipe1 r
WHERE NOT EXISTS (
    SELECT 1 FROM recipes re WHERE re.name = 'Chicken Rice Bowl' AND (r.user_id IS NULL AND re.user_id IS NULL OR r.user_id = re.user_id)
    )
    RETURNING id
    ),

-- get recipe1 id (either newly inserted or existing)
    recipe1_id AS (
SELECT id AS recipe_id FROM recipes WHERE name = 'Chicken Rice Bowl' LIMIT 1
    ),

-- find food_items ids to link (by name)
    fi_chicken AS (
SELECT id FROM food_items WHERE name = 'Chicken Breast (Grilled)' LIMIT 1
    ),
    fi_rice AS (
SELECT id FROM food_items WHERE name = 'Brown Rice (Cooked)' LIMIT 1
    ),
    fi_broccoli AS (
SELECT id FROM food_items WHERE name = 'Broccoli (Steamed)' LIMIT 1
    ),

-- insert ingredients for recipe1 (only if not already present)
    ins_recipe1_ing1 AS (
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.recipe_id, fc.id, 150, 'g', 'Grilled slices'
FROM recipe1_id r, fi_chicken fc
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.recipe_id AND ri.food_item_id = fc.id
    )
    RETURNING id
    ),

    ins_recipe1_ing2 AS (
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.recipe_id, fr.id, 200, 'g', 'Cooked'
FROM recipe1_id r, fi_rice fr
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.recipe_id AND ri.food_item_id = fr.id
    )
    RETURNING id
    ),

    ins_recipe1_ing3 AS (
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.recipe_id, fb.id, 100, 'g', 'Steamed'
FROM recipe1_id r, fi_broccoli fb
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.recipe_id AND ri.food_item_id = fb.id
    )
    RETURNING id
    ),

-- -------------------------
-- Recipe 2: Banana Smoothie
-- -------------------------
    recipe2 AS (
SELECT user_id FROM demo_user
    ),
    ins_recipe2 AS (
INSERT INTO recipes (id, user_id, name, servings, created_at)
SELECT gen_random_uuid(), r.user_id, 'Banana Smoothie', 1, now()
FROM recipe2 r
WHERE NOT EXISTS (
    SELECT 1 FROM recipes re WHERE re.name = 'Banana Smoothie' AND (r.user_id IS NULL AND re.user_id IS NULL OR r.user_id = re.user_id)
    )
    RETURNING id
    ),

    recipe2_id AS (
SELECT id AS recipe_id FROM recipes WHERE name = 'Banana Smoothie' LIMIT 1
    ),

    fi_banana AS (
SELECT id FROM food_items WHERE name = 'Banana' LIMIT 1
    ),
    fi_olive_oil AS (
SELECT id FROM food_items WHERE name = 'Olive Oil' LIMIT 1
    ),

    ins_recipe2_ing1 AS (
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.recipe_id, fb.id, 1, 'medium', 'Peel and blend'
FROM recipe2_id r, fi_banana fb
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.recipe_id AND ri.food_item_id = fb.id
    )
    RETURNING id
    ),

    ins_recipe2_ing2 AS (
INSERT INTO recipe_ingredients (id, recipe_id, food_item_id, amount, unit, note)
SELECT gen_random_uuid(), r.recipe_id, fo.id, 1, 'tsp', 'Optional healthy fat'
FROM recipe2_id r, fi_olive_oil fo
WHERE NOT EXISTS (
    SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.recipe_id AND ri.food_item_id = fo.id
    )
    RETURNING id
    )

-- Optional: show summary when run interactively
SELECT
    (SELECT COUNT(*) FROM recipes WHERE name IN ('Chicken Rice Bowl', 'Banana Smoothie')) AS recipes_count,
    (SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id IN (SELECT id FROM recipes WHERE name = 'Chicken Rice Bowl')) AS chicken_rice_ings,
    (SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id IN (SELECT id FROM recipes WHERE name = 'Banana Smoothie')) AS banana_smoothie_ings;
