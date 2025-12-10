
-- 1. Bảng nguyên liệu
CREATE TABLE IF NOT EXISTS ingredients (
                                           id BIGSERIAL PRIMARY KEY,
                                           name VARCHAR(255) UNIQUE NOT NULL,
                                           english_name VARCHAR(255),
                                           calories DOUBLE PRECISION,
                                           protein DOUBLE PRECISION,
                                           fat DOUBLE PRECISION,
                                           saturated_fat DOUBLE PRECISION,
                                           carbs DOUBLE PRECISION,
                                           sugar DOUBLE PRECISION,
                                           fiber DOUBLE PRECISION,
                                           sodium DOUBLE PRECISION,
                                           potassium DOUBLE PRECISION,
                                           cholesterol DOUBLE PRECISION,
                                           serving_size VARCHAR(100),
                                           note VARCHAR(500),
                                           image_url VARCHAR(500)
);

-- 2. Bảng công thức người dùng tạo
CREATE TABLE IF NOT EXISTS custom_recipes (
                                              id BIGSERIAL PRIMARY KEY,
                                              user_id VARCHAR(255) NOT NULL,
                                              name VARCHAR(255) NOT NULL,
                                              description TEXT,
                                              image_url VARCHAR(500),
                                              servings INTEGER DEFAULT 1,
                                              total_calories DOUBLE PRECISION,
                                              total_protein DOUBLE PRECISION,
                                              total_fat DOUBLE PRECISION,
                                              total_saturated_fat DOUBLE PRECISION,
                                              total_carbs DOUBLE PRECISION,
                                              total_sugar DOUBLE PRECISION,
                                              total_fiber DOUBLE PRECISION,
                                              total_sodium DOUBLE PRECISION,
                                              total_potassium DOUBLE PRECISION,
                                              total_cholesterol DOUBLE PRECISION,
                                              is_public BOOLEAN DEFAULT TRUE,
                                              likes_count INTEGER DEFAULT 0,
                                              created_at TIMESTAMP DEFAULT NOW()
);

-- 3. Bảng chi tiết nguyên liệu trong công thức
CREATE TABLE IF NOT EXISTS custom_recipe_ingredients (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         recipe_id BIGINT REFERENCES custom_recipes(id) ON DELETE CASCADE,
                                                         ingredient_id BIGINT REFERENCES ingredients(id),
                                                         quantity_gram DOUBLE PRECISION NOT NULL,
                                                         note VARCHAR(255)
);

-- 4. Thêm cột recipe_id vào meal_history
ALTER TABLE meal_history
    ADD COLUMN IF NOT EXISTS recipe_id BIGINT REFERENCES custom_recipes(id);