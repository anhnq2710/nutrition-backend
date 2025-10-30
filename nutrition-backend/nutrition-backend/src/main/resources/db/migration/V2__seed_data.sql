-- V2__seed_data.sql
-- Idempotent seed sample data for Nutrition App
-- Uses pgcrypto gen_random_uuid()

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Insert demo user (idempotent: if email exists, return existing id)
WITH user_upsert AS (
    INSERT INTO users (id, email, password_hash, roles, enabled, created_at)
        VALUES (
                   gen_random_uuid(),
                   'demo@nutritionapp.com',
                   -- password = '123456' -> bcrypt hash (for Spring Security testing)
                   '$2a$10$yHjCzNKhHg4C48VhE6tRzOiJw.Z7zKFL5o9u2FNsCG1o9Vv7dRVvG',
                   'ROLE_USER',
                   true,
                   now()
               )
        ON CONFLICT (email) DO UPDATE
            SET email = users.email
        RETURNING id AS user_id
),

-- If the user already existed, the INSERT ... ON CONFLICT ... DO UPDATE RETURNING will still return the existing id.
     user_result AS (
         SELECT user_id FROM user_upsert
     ),

-- Insert profile that references the user_result (insert only if not exists for that user)
     profile_ins AS (
         INSERT INTO profiles (id, user_id, height_cm, weight_kg, gender, date_of_birth, target_calories, goal, created_at, updated_at)
             SELECT
                 gen_random_uuid(),
                 ur.user_id,
                 175,
                 68.5,
                 'male',
                 '1995-07-20'::date,
                 2200,
                 'maintain',
                 now(),
                 now()
             FROM user_result ur
             WHERE NOT EXISTS (
                 SELECT 1 FROM profiles p WHERE p.user_id = ur.user_id
             )
             RETURNING id
     ),

-- Insert food items (we don't attempt to dedupe by name here; you can add ON CONFLICT if you want)
     food_ins AS (
         INSERT INTO food_items (id, name, brand, serving_description, serving_size_amount, serving_size_unit,
                                 calories, protein_g, carbs_g, fat_g, calories_per_100g, source, created_at, updated_at)
             VALUES
                 (gen_random_uuid(), 'Chicken Breast (Grilled)', 'Generic', '100 g', 100, 'g', 165, 31, 0, 3.6, 165, 'manual', now(), now()),
                 (gen_random_uuid(), 'Brown Rice (Cooked)', 'Generic', '100 g', 100, 'g', 111, 2.6, 23, 0.9, 111, 'manual', now(), now()),
                 (gen_random_uuid(), 'Broccoli (Steamed)', 'Generic', '100 g', 100, 'g', 35, 2.8, 7, 0.4, 35, 'manual', now(), now()),
                 (gen_random_uuid(), 'Olive Oil', 'Generic', '1 tbsp (13.5 g)', 13.5, 'g', 119, 0, 0, 13.5, 884, 'manual', now(), now()),
                 (gen_random_uuid(), 'Banana', 'Generic', '1 medium (118 g)', 118, 'g', 105, 1.3, 27, 0.3, 89, 'manual', now(), now())
             RETURNING id, name
     ),

-- Insert a meal for the user (only if not exists for user at approx same timestamp)
     meal_ins AS (
         INSERT INTO meals (id, user_id, name, meal_time, created_at)
             SELECT gen_random_uuid(), ur.user_id, 'Lunch', now(), now()
             FROM user_result ur
             WHERE NOT EXISTS (
                 SELECT 1 FROM meals m WHERE m.user_id = ur.user_id AND m.name = 'Lunch' AND m.meal_time::date = now()::date
             )
             RETURNING id AS meal_id, user_id
     ),

-- Insert meal items: link to food_items by name if possible, else leave null (custom)
     meal_item_chicken AS (
         INSERT INTO meal_items (id, meal_id, food_item_id, custom_name, quantity, unit, calories, protein_g, carbs_g, fat_g, created_at)
             SELECT
                 gen_random_uuid(),
                 mi.meal_id,
                 fi.id,
                 'Grilled Chicken Breast',
                 1,
                 'serving',
                 165,
                 31,
                 0,
                 3.6,
                 now()
             FROM meal_ins mi
                      LEFT JOIN food_ins fi ON fi.name = 'Chicken Breast (Grilled)'
             WHERE NOT EXISTS (
                 SELECT 1 FROM meal_items mit WHERE mit.meal_id = mi.meal_id AND mit.custom_name = 'Grilled Chicken Breast'
             )
             RETURNING id
     ),

     meal_item_rice AS (
         INSERT INTO meal_items (id, meal_id, food_item_id, custom_name, quantity, unit, calories, protein_g, carbs_g, fat_g, created_at)
             SELECT
                 gen_random_uuid(),
                 mi.meal_id,
                 fi.id,
                 'Brown Rice',
                 1,
                 'serving',
                 111,
                 2.6,
                 23,
                 0.9,
                 now()
             FROM meal_ins mi
                      LEFT JOIN food_ins fi ON fi.name = 'Brown Rice (Cooked)'
             WHERE NOT EXISTS (
                 SELECT 1 FROM meal_items mit WHERE mit.meal_id = mi.meal_id AND mit.custom_name = 'Brown Rice'
             )
             RETURNING id
     ),

-- Weight log (only if none for today)
     weight_ins AS (
         INSERT INTO weight_logs (id, user_id, weight_kg, recorded_at, created_at)
             SELECT gen_random_uuid(), ur.user_id, 68.5, now(), now()
             FROM user_result ur
             WHERE NOT EXISTS (
                 SELECT 1 FROM weight_logs w WHERE w.user_id = ur.user_id AND w.recorded_at::date = now()::date
             )
             RETURNING id
     ),

-- Activity log (only if none identical today)
     activity_ins AS (
         INSERT INTO activity_logs (id, user_id, name, duration_minutes, calories_burned, recorded_at, created_at)
             SELECT gen_random_uuid(), ur.user_id, 'Morning Run', 30, 280, now(), now()
             FROM user_result ur
             WHERE NOT EXISTS (
                 SELECT 1 FROM activity_logs a WHERE a.user_id = ur.user_id AND a.name = 'Morning Run' AND a.recorded_at::date = now()::date
             )
             RETURNING id
     ),

-- Goal (only if user has no similar active goal)
     goal_ins AS (
         INSERT INTO goals (id, user_id, type, target_value, start_date, end_date, created_at)
             SELECT gen_random_uuid(), ur.user_id, 'weight_loss', 65, current_date, (current_date + interval '60 days')::date, now()
             FROM user_result ur
             WHERE NOT EXISTS (
                 SELECT 1 FROM goals g WHERE g.user_id = ur.user_id AND g.type = 'weight_loss' AND (g.end_date IS NULL OR g.end_date >= current_date)
             )
             RETURNING id
     )

-- Final select to show what was inserted (optional, useful when running interactively)
SELECT
    (SELECT user_id FROM user_result) AS seeded_user_id,
    (SELECT COUNT(*) FROM food_ins) AS seeded_food_count;
