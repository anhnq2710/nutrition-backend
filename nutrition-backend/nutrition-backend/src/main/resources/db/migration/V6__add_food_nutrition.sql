-- Bước 1: Tạo bảng nếu chưa tồn tại (với UNIQUE constraint rõ ràng)
CREATE TABLE IF NOT EXISTS food_nutrition (
                                              id BIGSERIAL PRIMARY KEY,
                                              name VARCHAR(255) NOT NULL,
                                              english_name VARCHAR(255),
                                              calories DECIMAL(8,2),
                                              protein DECIMAL(6,2),
                                              fat DECIMAL(6,2),
                                              saturated_fat DECIMAL(6,2),
                                              carbs DECIMAL(6,2),
                                              sugar DECIMAL(6,2),
                                              fiber DECIMAL(6,2),
                                              sodium DECIMAL(8,2),
                                              potassium DECIMAL(8,2),
                                              cholesterol DECIMAL(8,2),
                                              serving_size VARCHAR(100),
                                              note VARCHAR(255)
);

-- Bước 2: Thêm UNIQUE constraint trên name (nếu chưa có)
ALTER TABLE food_nutrition ADD CONSTRAINT food_nutrition_name_key UNIQUE (name);

-- Bước 4: Insert 30 món (ON CONFLICT DO NOTHING để an toàn)
INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Phở bò', 'Beef Pho', 167, 8.4, 4.2, 1.8, 23.3, 2.1, 1.2, 400, 267, 17, '1 tô (300g)', 'Phù hợp tiểu đường nếu không ăn hết nước dùng')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bún chả', 'Bun Cha', 180, 8.0, 4.8, 1.6, 26.0, 1.2, 1.5, 320, 240, 16, '1 dĩa (250g)', 'Natri cao – tránh nếu huyết áp cao')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh mì pate', 'Banh Mi Pate', 140, 4.8, 7.2, 3.2, 16.0, 0.8, 0.8, 240, 120, 12, '1 ổ (200g)', 'Chất béo cao – chỉ 1/2 ổ cho tim mạch')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cơm tấm', 'Com Tam', 240, 12.0, 10.0, 4.0, 28.0, 0.4, 1.6, 360, 200, 32, '1 dĩa (400g)', 'Protein tốt cho gan nhiễm mỡ')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh xèo', 'Banh Xeo', 160, 6.0, 8.0, 2.8, 18.0, 0.2, 0.8, 280, 160, 24, '1 cái (200g)', 'Calo cao – giảm nếu giảm cân')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Hủ tiếu', 'Hu Tieu', 192, 7.2, 5.6, 2.0, 27.2, 1.6, 1.2, 400, 280, 18, '1 tô (300g)', 'Natri cao – ít nước dùng')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Chè ba màu', 'Che Ba Mau', 120, 2.0, 3.2, 1.2, 20.0, 12.0, 0.4, 20, 80, 4, '1 chén (150g)', 'Đường rất cao – tránh tiểu đường')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bò kho', 'Bo Kho', 220, 11.2, 8.8, 3.6, 22.0, 0.8, 0.8, 440, 360, 28, '1 tô (350g)', 'Protein tốt cho suy thận')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cá kho tộ', 'Ca Kho To', 140, 14.0, 6.0, 2.0, 2.0, 0.0, 0.0, 520, 320, 40, '1 miếng (150g)', 'Natri cao – giảm nước mắm')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Gỏi cuốn', 'Goi Cuon', 80, 4.0, 2.0, 0.4, 10.0, 0.4, 0.8, 160, 120, 8, '4 cuốn (200g)', 'Thấp calo – phù hợp giảm cân')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Nem nướng', 'Nem Nuong', 168, 7.2, 8.0, 2.8, 18.0, 0.8, 0.4, 340, 180, 20, '1 phần (250g)', 'Chất béo cao – tránh tim mạch')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh canh', 'Banh Canh', 152, 4.8, 4.0, 1.6, 24.0, 1.2, 0.8, 380, 240, 12, '1 tô (300g)', 'Natri cao – ít nước dùng')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cháo gà', 'Chao Ga', 112, 6.0, 3.2, 0.8, 14.0, 0.4, 1.2, 240, 160, 10, '1 tô (250g)', 'Protein tốt cho suy thận')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh cuốn', 'Banh Cuon', 100, 3.2, 2.4, 0.8, 16.0, 0.4, 0.8, 200, 100, 6, '1 đĩa (200g)', 'Thấp calo – phù hợp giảm cân')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Mì Quảng', 'Mi Quang', 208, 8.8, 7.2, 2.4, 28.0, 1.6, 1.6, 400, 280, 16, '1 tô (350g)', 'Carbs cao – giảm cơm cho tiểu đường')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bún riêu', 'Bun Rieu', 152, 7.2, 4.8, 1.6, 20.0, 1.2, 0.8, 360, 200, 14, '1 tô (300g)', 'Natri cao – tránh huyết áp')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cà phê sữa đá', 'Ca Phe Sua Da', 60, 1.2, 2.0, 1.2, 10.0, 8.0, 0.0, 40, 80, 4, '1 ly (250ml)', 'Đường cao – tránh tiểu đường')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh flan', 'Banh Flan', 80, 2.4, 3.2, 1.6, 11.2, 10.0, 0.0, 32, 60, 20, '1 cái (100g)', 'Đường cao – chỉ 1/2 cái')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Gỏi gà', 'Goi Ga', 100, 10.0, 4.0, 1.2, 6.0, 0.4, 0.8, 160, 200, 24, '1 đĩa (200g)', 'Protein tốt cho gan nhiễm mỡ')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh tét', 'Banh Tet', 180, 4.0, 8.0, 3.2, 24.0, 0.8, 1.2, 200, 120, 8, '1 lát (150g)', 'Calo cao – giảm nếu giảm cân')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bún mọc', 'Bun Moc', 128, 6.0, 3.2, 1.0, 20.0, 0.8, 0.8, 280, 160, 10, '1 tô (250g)', 'Natri cao – ít nước dùng')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cơm chiên', 'Com Chien', 220, 4.8, 10.0, 3.6, 28.0, 0.4, 0.8, 320, 120, 16, '1 đĩa (300g)', 'Chất béo cao – tránh tim mạch')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh mì thịt', 'Banh Mi Thit', 160, 6.0, 8.0, 3.2, 18.0, 0.8, 0.8, 280, 160, 20, '1 ổ (250g)', 'Protein tốt cho suy thận')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Nem cua bể', 'Nem Cua Be', 120, 4.8, 6.0, 2.0, 12.0, 0.4, 0.4, 240, 140, 12, '5 cái (150g)', 'Calo vừa – phù hợp')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh tráng nướng', 'Banh Trang Nuong', 140, 3.2, 7.2, 2.8, 16.0, 1.2, 0.4, 200, 80, 8, '1 cái (100g)', 'Chất béo cao – giảm nếu tim mạch')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Cơm gà', 'Com Ga', 220, 12.0, 8.0, 2.8, 26.0, 0.4, 1.2, 360, 240, 32, '1 dĩa (400g)', 'Protein tốt cho gan nhiễm mỡ')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bún bò Huế', 'Bun Bo Hue', 192, 8.0, 6.4, 2.4, 26.0, 1.6, 1.2, 560, 280, 18, '1 tô (350g)', 'Natri rất cao – tránh huyết áp')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh khoái', 'Banh Khoai', 168, 4.8, 8.8, 3.6, 20.0, 0.8, 0.8, 280, 120, 14, '1 cái (200g)', 'Calo cao – giảm khẩu phần')
ON CONFLICT (name) DO NOTHING;

INSERT INTO food_nutrition (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note) VALUES
    ('Bánh ít lá gai', 'Banh It La Gai', 112, 1.6, 4.0, 1.6, 18.0, 8.0, 0.4, 40, 60, 6, '1 cái (100g)', 'Đường cao – tránh tiểu đường')
ON CONFLICT (name) DO NOTHING;