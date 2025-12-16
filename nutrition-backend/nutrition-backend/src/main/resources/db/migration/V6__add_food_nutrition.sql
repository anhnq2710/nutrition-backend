-- BƯỚC 1: Thêm 2 cột mới vào bảng food_nutrition
ALTER TABLE food_nutrition
    ADD COLUMN IF NOT EXISTS is_per_100g BOOLEAN DEFAULT TRUE;

ALTER TABLE food_nutrition
    ADD COLUMN IF NOT EXISTS serving_multiplier DECIMAL(6,2) DEFAULT 1.0;

-- BƯỚC 2: Insert 30 món – ĐÃ CHUẨN HÓA THEO 100G
-- calories, protein, fat... là cho 100g
-- serving_multiplier = khối lượng serving / 100
-- image_url giả định (bạn thay bằng thật nếu có)

INSERT INTO food_nutrition (
    name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol,
    serving_size, note, image_url, is_per_100g, serving_multiplier
) VALUES
      ('Phở bò', 'Beef Pho', 55.67, 2.8, 1.4, 0.6, 7.77, 0.7, 0.4, 133.33, 89.0, 5.67,
       '1 tô (300g)', 'Phù hợp tiểu đường nếu không ăn hết nước dùng', 'food-images/Phở bò.jpg', TRUE, 3.00),
      ('Bún chả', 'Bun Cha', 72.0, 3.2, 1.92, 0.64, 10.4, 0.48, 0.6, 128.0, 96.0, 6.4,
       '1 dĩa (250g)', 'Natri cao – tránh nếu huyết áp cao', 'food-images/Bún chả.jpg', TRUE, 2.50),
      ('Bánh mì pate', 'Banh Mi Pate', 70.0, 2.4, 3.6, 1.6, 8.0, 0.4, 0.4, 120.0, 60.0, 6.0,
       '1 ổ (200g)', 'Chất béo cao – chỉ 1/2 ổ cho tim mạch', 'food-images/bánh mì pate.jpg', TRUE, 2.00),
      ('Cơm tấm', 'Com Tam', 60.0, 3.0, 2.5, 1.0, 7.0, 0.1, 0.4, 90.0, 50.0, 8.0,
       '1 dĩa (400g)', 'Protein tốt cho gan nhiễm mỡ', 'food-images/Cơm tấm.jpg', TRUE, 4.00),
      ('Bánh xèo', 'Banh Xeo', 80.0, 3.0, 4.0, 1.4, 9.0, 0.1, 0.4, 140.0, 80.0, 12.0,
       '1 cái (200g)', 'Calo cao – giảm nếu giảm cân', 'food-images/bánh xèo.jpg', TRUE, 2.00),
      ('Hủ tiếu', 'Hu Tieu', 64.0, 2.4, 1.87, 0.67, 9.07, 0.53, 0.4, 133.33, 93.33, 6.0,
       '1 tô (300g)', 'Natri cao – ít nước dùng', 'food-images/Hủ tiếu.jpg', TRUE, 3.00),
      ('Chè ba màu', 'Che Ba Mau', 80.0, 1.33, 2.13, 0.8, 13.33, 8.0, 0.27, 13.33, 53.33, 2.67,
       '1 chén (150g)', 'Đường rất cao – tránh tiểu đường', 'food-images/Chè ba màu.jpg', TRUE, 1.50),
      ('Bò kho', 'Bo Kho', 62.86, 3.2, 2.51, 1.03, 6.29, 0.23, 0.23, 125.71, 102.86, 8.0,
       '1 tô (350g)', 'Protein tốt cho suy thận', 'food-images/Bò kho.jpg', TRUE, 3.50),
      ('Cá kho tộ', 'Ca Kho To', 93.33, 9.33, 4.0, 1.33, 1.33, 0.0, 0.0, 346.67, 213.33, 26.67,
       '1 miếng (150g)', 'Natri cao – giảm nước mắm', 'food-images/Cá kho tộ.jpg', TRUE, 1.50),
      ('Gỏi cuốn', 'Goi Cuon', 40.0, 2.0, 1.0, 0.2, 5.0, 0.2, 0.4, 80.0, 60.0, 4.0,
       '4 cuốn (200g)', 'Thấp calo – phù hợp giảm cân', 'food-images/Gỏi cuốn.jpg', TRUE, 2.00),
      ('Nem nướng', 'Nem Nuong', 67.2, 2.88, 3.2, 1.12, 7.2, 0.32, 0.16, 136.0, 72.0, 8.0,
       '1 phần (250g)', 'Chất béo cao – tránh tim mạch', 'food-images/Nem nướng.jpg', TRUE, 2.50),
      ('Bánh canh', 'Banh Canh', 50.67, 1.6, 1.33, 0.53, 8.0, 0.4, 0.27, 126.67, 80.0, 4.0,
       '1 tô (300g)', 'Natri cao – ít nước dùng', 'food-images/Bánh canh.jpg', TRUE, 3.00),
      ('Cháo gà', 'Chao Ga', 44.8, 2.4, 1.28, 0.32, 5.6, 0.16, 0.48, 96.0, 64.0, 4.0,
       '1 tô (250g)', 'Protein tốt cho suy thận', 'food-images/Cháo gà.jpg', TRUE, 2.50),
      ('Bánh cuốn', 'Banh Cuon', 50.0, 1.6, 1.2, 0.4, 8.0, 0.2, 0.4, 100.0, 50.0, 3.0,
       '1 đĩa (200g)', 'Thấp calo – phù hợp giảm cân', 'food-images/Bánh cuốn.jpg', TRUE, 2.00),
      ('Mì Quảng', 'Mi Quang', 59.43, 2.51, 2.06, 0.69, 8.0, 0.46, 0.46, 114.29, 80.0, 4.57,
       '1 tô (350g)', 'Carbs cao – giảm cơm cho tiểu đường', 'food-images/Mì Quảng.jpg', TRUE, 3.50),
      ('Bún riêu', 'Bun Rieu', 50.67, 2.4, 1.6, 0.53, 6.67, 0.4, 0.27, 120.0, 66.67, 4.67,
       '1 tô (300g)', 'Natri cao – tránh huyết áp', 'food-images/Bún riêu.jpg', TRUE, 3.00),
      ('Cà phê sữa đá', 'Ca Phe Sua Da', 24.0, 0.48, 0.8, 0.48, 4.0, 3.2, 0.0, 16.0, 32.0, 1.6,
       '1 ly (250ml)', 'Đường cao – tránh tiểu đường', 'food-images/Cà phê sữa đá.jpg', TRUE, 2.50),
      ('Bánh flan', 'Banh Flan', 80.0, 2.4, 3.2, 1.6, 11.2, 10.0, 0.0, 32.0, 60.0, 20.0,
       '1 cái (100g)', 'Đường cao – chỉ 1/2 cái', 'food-images/Bánh flan.jpg', TRUE, 1.00),
      ('Gỏi gà', 'Goi Ga', 50.0, 5.0, 2.0, 0.6, 3.0, 0.2, 0.4, 80.0, 100.0, 12.0,
       '1 đĩa (200g)', 'Protein tốt cho gan nhiễm mỡ', 'food-images/Gỏi gà.jpg', TRUE, 2.00),
      ('Bánh tét', 'Banh Tet', 120.0, 2.67, 5.33, 2.13, 16.0, 0.53, 0.8, 133.33, 80.0, 5.33,
       '1 lát (150g)', 'Calo cao – giảm nếu giảm cân', 'food-images/Bánh tét.jpg', TRUE, 1.50),
      ('Bún mọc', 'Bun Moc', 51.2, 2.4, 1.28, 0.4, 8.0, 0.32, 0.32, 112.0, 64.0, 4.0,
       '1 tô (250g)', 'Natri cao – ít nước dùng', 'food-images/Bún mọc.jpg', TRUE, 2.50),
      ('Cơm chiên', 'Com Chien', 73.33, 1.6, 3.33, 1.2, 9.33, 0.13, 0.27, 106.67, 40.0, 5.33,
       '1 đĩa (300g)', 'Chất béo cao – tránh tim mạch', 'food-images/Cơm chiên.jpg', TRUE, 3.00),
      ('Bánh mì thịt', 'Banh Mi Thit', 64.0, 2.4, 3.2, 1.28, 7.2, 0.32, 0.32, 112.0, 64.0, 8.0,
       '1 ổ (250g)', 'Protein tốt cho suy thận', 'food-images/Bánh mì thịt.jpg', TRUE, 2.50),
      ('Nem cua bể', 'Nem Cua Be', 80.0, 3.2, 4.0, 1.33, 8.0, 0.27, 0.27, 160.0, 93.33, 8.0,
       '5 cái (150g)', 'Calo vừa – phù hợp', 'food-images/Nem cua bể.jpg', TRUE, 1.50),
      ('Bánh tráng nướng', 'Banh Trang Nuong', 140.0, 3.2, 7.2, 2.8, 16.0, 1.2, 0.4, 200.0, 80.0, 8.0,
       '1 cái (100g)', 'Chất béo cao – giảm nếu tim mạch', 'food-images/Bánh tráng nướng.jpg', TRUE, 1.00),
      ('Cơm gà', 'Com Ga', 55.0, 3.0, 2.0, 0.7, 6.5, 0.1, 0.3, 90.0, 60.0, 8.0,
       '1 dĩa (400g)', 'Protein tốt cho gan nhiễm mỡ', 'food-images/Cơm gà.jpg', TRUE, 4.00),
      ('Bún bò Huế', 'Bun Bo Hue', 54.86, 2.29, 1.83, 0.69, 7.43, 0.46, 0.34, 160.0, 80.0, 5.14,
       '1 tô (350g)', 'Natri rất cao – tránh huyết áp', 'food-images/Bún bò Huế.jpg', TRUE, 3.50),
      ('Bánh khoái', 'Banh Khoai', 84.0, 2.4, 4.4, 1.8, 10.0, 0.4, 0.4, 140.0, 60.0, 7.0,
       '1 cái (200g)', 'Calo cao – giảm khẩu phần', 'food-images/Bánh khoái.jpg', TRUE, 2.00),
      ('Bánh ít lá gai', 'Banh It La Gai', 112.0, 1.6, 4.0, 1.6, 18.0, 8.0, 0.4, 40.0, 60.0, 6.0,
       '1 cái (100g)', 'Đường cao – tránh tiểu đường', 'food-images/Bánh ít lá gai.jpg', TRUE, 1.00)
ON CONFLICT (name) DO UPDATE SET
                                 english_name = EXCLUDED.english_name,
                                 calories = EXCLUDED.calories,
                                 protein = EXCLUDED.protein,
                                 fat = EXCLUDED.fat,
                                 saturated_fat = EXCLUDED.saturated_fat,
                                 carbs = EXCLUDED.carbs,
                                 sugar = EXCLUDED.sugar,
                                 fiber = EXCLUDED.fiber,
                                 sodium = EXCLUDED.sodium,
                                 potassium = EXCLUDED.potassium,
                                 cholesterol = EXCLUDED.cholesterol,
                                 serving_size = EXCLUDED.serving_size,
                                 note = EXCLUDED.note,
                                 image_url = EXCLUDED.image_url,
                                 is_per_100g = EXCLUDED.is_per_100g,
                                 serving_multiplier = EXCLUDED.serving_multiplier;