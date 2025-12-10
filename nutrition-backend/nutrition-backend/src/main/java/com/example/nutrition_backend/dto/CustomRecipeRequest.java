package com.example.nutrition_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CustomRecipeRequest {
    private String name;
    private String description;
    private Integer servings = 1;
    private boolean isPublic = true;
    private List<RecipeIngredientReq> ingredients;

    @Data
    public static class RecipeIngredientReq {
        private Long ingredientId;
        private Double quantityGram;
        private String note;
    }
}