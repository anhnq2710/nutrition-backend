

-- 30 nguyên liệu cơ bản
INSERT INTO ingredients (name, english_name, calories, protein, fat, saturated_fat, carbs, sugar, fiber, sodium, potassium, cholesterol, serving_size, note, image_url) VALUES
                                                                                                                                                                            ('Thịt bò nạc', 'Lean Beef', 250, 26, 15, 6, 0, 0, 0, 75, 350, 80, '100g', 'Phù hợp tăng cơ', '/food-images/beef.jpg'),
                                                                                                                                                                            ('Thịt gà (ức)', 'Chicken Breast', 165, 31, 3.6, 1, 0, 0, 0, 74, 256, 85, '100g', 'Ít calo, nhiều protein', '/food-images/chicken.jpg'),
                                                                                                                                                                            ('Cá hồi', 'Salmon', 208, 20, 13, 2.5, 0, 0, 0, 59, 363, 55, '100g', 'Giàu Omega-3', '/food-images/salmon.jpg'),
                                                                                                                                                                            ('Trứng gà', 'Egg', 155, 13, 11, 3.3, 1.1, 0.6, 0, 124, 138, 373, '1 quả (50g)', 'Tốt cho cơ bắp', '/food-images/egg.jpg'),
                                                                                                                                                                            ('Gạo trắng', 'White Rice', 130, 2.7, 0.3, 0, 28, 0.1, 0.3, 1, 30, 0, '100g', 'Năng lượng chính', '/food-images/rice.jpg'),
                                                                                                                                                                            ('Rau cải xanh', 'Green Vegetables', 25, 2.9, 0.4, 0, 5, 2.5, 2.5, 66, 436, 0, '100g', 'Ít calo, nhiều vitamin', '/food-images/vegetable.jpg'),
                                                                                                                                                                            ('Cà rốt', 'Carrot', 41, 0.9, 0.2, 0, 9.6, 4.7, 2.8, 69, 320, 0, '100g', 'Tốt cho mắt', '/food-images/carrot.jpg'),
                                                                                                                                                                            ('Hành tây', 'Onion', 40, 1.1, 0.1, 0, 9.3, 4.2, 1.7, 4, 146, 0, '100g', 'Tăng hương vị', '/food-images/onion.jpg'),
                                                                                                                                                                            ('Tỏi', 'Garlic', 149, 6.4, 0.5, 0, 33, 1, 2.1, 17, 401, 0, '100g', 'Kháng viêm', '/food-images/garlic.jpg'),
                                                                                                                                                                            ('Dầu ô liu', 'Olive Oil', 884, 0, 100, 14, 0, 0, 0, 2, 0, 0, '100g', 'Chất béo tốt', '/food-images/olive-oil.jpg')
    ON CONFLICT (name) DO NOTHING;

