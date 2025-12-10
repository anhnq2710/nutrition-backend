package com.example.nutrition_backend.dto;

import com.example.nutrition_backend.entity.CustomRecipe;
import lombok.Data;
import java.util.List;

@Data
public class CustomRecipeDetail {
    private CustomRecipe recipe;
    private List<IngredientDetail> ingredients;

    @Data
    public static class IngredientDetail {
        private String ingredientName;
        private String englishName;
        private Double quantityGram;
        private String note;
        private String imageUrl;
    }
}