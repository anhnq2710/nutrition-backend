-- Xóa foreign key cũ
ALTER TABLE custom_recipe_ingredients
    DROP CONSTRAINT IF EXISTS custom_recipe_ingredients_ingredient_id_fkey;

-- Xóa foreign key mới nếu đã tồn tại
ALTER TABLE custom_recipe_ingredients
    DROP CONSTRAINT IF EXISTS fk_custom_recipe_ingredient_food;

-- Thêm foreign key mới
ALTER TABLE custom_recipe_ingredients
    ADD CONSTRAINT fk_custom_recipe_ingredient_food
        FOREIGN KEY (ingredient_id) REFERENCES food_nutrition(id) ON DELETE CASCADE;