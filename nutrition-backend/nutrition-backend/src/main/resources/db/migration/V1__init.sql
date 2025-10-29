-- V1__init.sql
-- Flyway baseline migration: create initial schema for nutrition app
-- Uses gen_random_uuid() from pgcrypto extension for UUID primary keys

-- Create extension for UUID generation (pgcrypto provides gen_random_uuid)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ========== users ==========
CREATE TABLE IF NOT EXISTS users (
                                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(320) NOT NULL UNIQUE,
    password_hash varchar(200) NOT NULL,
    roles varchar(200),
    enabled boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- ========== profiles (one-to-one with users) ==========
CREATE TABLE IF NOT EXISTS profiles (
                                        id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL UNIQUE,
    height_cm integer,
    weight_kg numeric(6,2),
    gender varchar(32),
    date_of_birth date,
    target_calories integer,
    goal varchar(64),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- ========== food_items ==========
CREATE TABLE IF NOT EXISTS food_items (
                                          id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(255) NOT NULL,
    brand varchar(255),
    serving_description varchar(255),
    serving_size_amount numeric(12,3),
    serving_size_unit varchar(64),
    calories numeric(10,2),
    protein_g numeric(10,2),
    carbs_g numeric(10,2),
    fat_g numeric(10,2),
    calories_per_100g numeric(10,2),
    source varchar(128),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_food_items_name ON food_items(name);

-- ========== meals ==========
CREATE TABLE IF NOT EXISTS meals (
                                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    name varchar(128),
    meal_time timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_meals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_meals_user_time ON meals(user_id, meal_time);

-- ========== meal_items ==========
CREATE TABLE IF NOT EXISTS meal_items (
                                          id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    meal_id uuid NOT NULL,
    food_item_id uuid,
    custom_name varchar(255),
    quantity numeric(12,3) NOT NULL DEFAULT 1,
    unit varchar(64),
    calories numeric(10,2),
    protein_g numeric(10,2),
    carbs_g numeric(10,2),
    fat_g numeric(10,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_meal_items_meal FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_items_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL
    );

-- ========== food_logs ==========
CREATE TABLE IF NOT EXISTS food_logs (
                                         id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    food_item_id uuid,
    custom_name varchar(255),
    quantity numeric(12,3) NOT NULL DEFAULT 1,
    unit varchar(64),
    calories numeric(10,2),
    protein_g numeric(10,2),
    carbs_g numeric(10,2),
    fat_g numeric(10,2),
    logged_at timestamptz NOT NULL DEFAULT now(),
    meal_id uuid,
    notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_food_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_food_logs_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL,
    CONSTRAINT fk_food_logs_meal FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_food_logs_user_logged_at ON food_logs(user_id, logged_at);

-- ========== recipes ==========
CREATE TABLE IF NOT EXISTS recipes (
                                       id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid,
    name varchar(255) NOT NULL,
    servings integer NOT NULL DEFAULT 1,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_recipes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
    );

-- ========== recipe_ingredients ==========
CREATE TABLE IF NOT EXISTS recipe_ingredients (
                                                  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    recipe_id uuid NOT NULL,
    food_item_id uuid,
    amount numeric(12,3),
    unit varchar(64),
    note varchar(512),
    CONSTRAINT fk_recipe_ings_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_recipe_ings_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL
    );

-- ========== weight_logs ==========
CREATE TABLE IF NOT EXISTS weight_logs (
                                           id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    weight_kg numeric(7,3) NOT NULL,
    recorded_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_weight_user_time ON weight_logs(user_id, recorded_at);

-- ========== activity_logs ==========
CREATE TABLE IF NOT EXISTS activity_logs (
                                             id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    name varchar(255),
    duration_minutes integer,
    calories_burned numeric(10,2),
    recorded_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- ========== goals ==========
CREATE TABLE IF NOT EXISTS goals (
                                     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    type varchar(64),
    target_value numeric(12,3),
    start_date date,
    end_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Optional: helpful views or materialized views can be added later.

