// src/main/java/com/example/nutrition_backend/controller/CustomRecipeController.java
package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.dto.CustomRecipeRequest;
import com.example.nutrition_backend.dto.CustomRecipeDetail;
import com.example.nutrition_backend.entity.CustomRecipe;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.service.CustomRecipeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class CustomRecipeController {

    private final CustomRecipeService recipeService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "1") Integer servings,
            @RequestParam(defaultValue = "true") boolean isPublic,
            @RequestParam("ingredients") String ingredientsJson,
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<CustomRecipeRequest.RecipeIngredientReq> list = mapper.readValue(ingredientsJson, new TypeReference<>() {});

        CustomRecipeRequest req = new CustomRecipeRequest();
        req.setName(name);
        req.setDescription(description);
        req.setServings(servings);
        req.setPublic(isPublic);
        req.setIngredients(list);

        CustomRecipe result = recipeService.createRecipe(userId, req, image);
        return ResponseEntity.ok(Map.of(
                "message", "Tạo công thức thành công!",
                "recipeId", result.getId()
        ));
    }

    @PostMapping("/eat-custom")
    public ResponseEntity<MealHistory> eatCustom(
            @RequestParam String userId,
            @RequestParam Long recipeId,
            @RequestParam(required = false) String mealType) {  // Có thể gửi: BREAKFAST, LUNCH, DINNER, SNACK

        return ResponseEntity.ok(recipeService.eatCustomRecipe(userId, recipeId, mealType));
    }

    @GetMapping("/public")
    public List<CustomRecipe> getPublic() {
        return recipeService.getPublicRecipes();
    }

    @GetMapping("/my")
    public List<CustomRecipe> getMy(@RequestParam String userId) {
        return recipeService.getMyRecipes(userId);
    }

    @GetMapping("/{id}")
    public CustomRecipeDetail getDetail(@PathVariable Long id) {
        return recipeService.getRecipeDetail(id);
    }
}