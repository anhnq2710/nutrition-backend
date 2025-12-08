-- Bước 1: Tạo bảng health_profile nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS health_profile (
                                              id BIGSERIAL PRIMARY KEY,
                                              user_id VARCHAR(255) UNIQUE NOT NULL,
    has_diabetes BOOLEAN DEFAULT FALSE,
    diabetes_type VARCHAR(10),
    hba1c DECIMAL(4,2),
    has_hypertension BOOLEAN DEFAULT FALSE,
    blood_pressure_systolic INT,
    blood_pressure_diastolic INT,
    has_cardiovascular BOOLEAN DEFAULT FALSE,
    cholesterol_total DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    height_cm INT,
    age INT,
    gender VARCHAR(10),
    activity_level VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );


-- Insert dữ liệu mẫu (1 user test với tiểu đường + huyết áp cao)
DELETE FROM health_profile;  -- Xóa cũ nếu có
INSERT INTO health_profile (user_id, has_diabetes, diabetes_type, hba1c, has_hypertension, blood_pressure_systolic, blood_pressure_diastolic, has_cardiovascular, cholesterol_total, weight_kg, height_cm, age, gender, activity_level) VALUES
    ('test123', true, 'type2', 8.5, true, 150, 95, false, 220, 68, 165, 55, 'male', 'moderate')
    ON CONFLICT (user_id) DO NOTHING;

-- Bước 2: Tạo bảng meal_history nếu chưa tồn tại
CREATE TABLE IF NOT EXISTS meal_history (
                                            id BIGSERIAL PRIMARY KEY,
                                            user_id VARCHAR(255) NOT NULL,
    food_name VARCHAR(255) NOT NULL,
    calories DECIMAL(8,2),
    protein DECIMAL(6,2),
    fat DECIMAL(6,2),
    carbs DECIMAL(6,2),
    sugar DECIMAL(6,2),
    sodium DECIMAL(8,2),
    meal_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Thêm index để query nhanh
CREATE INDEX IF NOT EXISTS idx_meal_user_date ON meal_history(user_id, meal_date);

-- Insert dữ liệu mẫu (7 bữa ăn 7 ngày, với đường/natri cao để trigger lời khuyên)
DELETE FROM meal_history;  -- Xóa cũ nếu có
INSERT INTO meal_history (user_id, food_name, calories, protein, fat, carbs, sugar, sodium, meal_date) VALUES
                                                                                                           ('test123', 'Phở bò', 500, 25, 15, 60, 5, 1200, '2025-12-01'),
                                                                                                           ('test123', 'Chè ba màu', 300, 5, 8, 50, 30, 50, '2025-12-02'),
                                                                                                           ('test123', 'Bún chả', 450, 20, 12, 65, 3, 800, '2025-12-03'),
                                                                                                           ('test123', 'Bánh mì pate', 350, 12, 18, 40, 2, 600, '2025-12-04'),
                                                                                                           ('test123', 'Cơm tấm', 600, 30, 25, 70, 1, 900, '2025-12-05'),
                                                                                                           ('test123', 'Hủ tiếu', 480, 18, 14, 68, 4, 1000, '2025-12-06'),
                                                                                                           ('test123', 'Bò kho', 550, 28, 22, 55, 2, 1100, '2025-12-07')
    ON CONFLICT DO NOTHING;