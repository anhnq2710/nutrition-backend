-- Tạo bảng disease_limits nếu chưa có
CREATE TABLE IF NOT EXISTS disease_limits (
    id BIGSERIAL PRIMARY KEY,
    disease_name VARCHAR(50) UNIQUE NOT NULL,
    viet_name VARCHAR(100) NOT NULL,
    sugar_max DECIMAL(6,2),
    sodium_max DECIMAL(8,2),
    fat_max DECIMAL(6,2),
    calorie_max DECIMAL(8,2),
    note VARCHAR(255)
);
-- Dữ liệu mẫu cho 3 bệnh
INSERT INTO disease_limits (disease_name, viet_name, sugar_max, sodium_max, fat_max, calorie_max, note) VALUES
('diabetes', 'Tiểu đường', 50.0, 2300.0, 65.0, 2000.0, 'Tránh chè, nước ngọt. Ăn rau củ ít đường.'),
('hypertension', 'Huyết áp cao', 25.0, 1500.0, 65.0, 2000.0, 'Hạn chế muối, nước mắm. Uống trà thảo mộc.'),
('cardiovascular', 'Tim mạch', 25.0, 2300.0, 20.0, 2000.0, 'Giảm mỡ động vật, đồ chiên. Tăng omega-3.')
ON CONFLICT (disease_name) DO NOTHING;

ALTER TABLE health_profile ADD COLUMN disease_id BIGINT REFERENCES disease_limits(id);

-- Cập nhật profile test123 với disease_id = 1 (diabetes)
UPDATE health_profile SET disease_id = 1 WHERE user_id = 'test123';