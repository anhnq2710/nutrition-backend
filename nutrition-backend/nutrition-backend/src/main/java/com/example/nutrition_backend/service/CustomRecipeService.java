// src/main/java/com/example/nutrition_backend/service/CustomRecipeService.java
package com.example.nutrition_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.nutrition_backend.dto.CustomRecipeRequest;
import com.example.nutrition_backend.dto.CustomRecipeDetail;
import com.example.nutrition_backend.dto.MealType;
import com.example.nutrition_backend.entity.*;
import com.example.nutrition_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomRecipeService {

    private final CustomRecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;
    private final CustomRecipeIngredientRepository recipeIngredientRepo;
    private final MealHistoryRepository mealHistoryRepo;
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dinzelnoh",
            "api_key", "761534765551952",
            "api_secret", "zWp_dTM2NFJv2M3vSfUum4BXDhU",
            "secure", true
    ));

    public CustomRecipe createRecipe(String userId, CustomRecipeRequest request, MultipartFile image) throws Exception {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                // Upload lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) uploadResult.get("secure_url"); // URL HTTPS từ Cloudinary
            } catch (Exception e) {
                throw new RuntimeException("Upload ảnh công thức lên Cloudinary thất bại: " + e.getMessage());
            }
        }

        CustomRecipe recipe = new CustomRecipe();
        recipe.setUserId(userId);
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setServings(request.getServings());
        recipe.setPublic(request.isPublic());
        recipe.setImageUrl(imageUrl); // URL từ Cloudinary

        // Tính tổng dinh dưỡng
        double totalCal = 0, totalPro = 0, totalFat = 0, totalSatFat = 0;
        double totalCarbs = 0, totalSugar = 0, totalFiber = 0, totalSodium = 0;
        double totalPot = 0, totalChol = 0;

        List<CustomRecipeIngredient> ingredients = new ArrayList<>();
        for (CustomRecipeRequest.RecipeIngredientReq item : request.getIngredients()) {
            FoodEntity ing = ingredientRepo.findById(item.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu ID: " + item.getIngredientId()));

            double ratio = item.getQuantityGram() / 100.0;

            totalCal += ing.getCalories() * ratio;
            totalPro += ing.getProtein() * ratio;
            totalFat += ing.getFat() * ratio;
            totalSatFat += ing.getSaturatedFat() * ratio;
            totalCarbs += ing.getCarbs() * ratio;
            totalSugar += ing.getSugar() * ratio;
            totalFiber += ing.getFiber() * ratio;
            totalSodium += ing.getSodium() * ratio;
            totalPot += ing.getPotassium() * ratio;
            totalChol += ing.getCholesterol() * ratio;

            CustomRecipeIngredient cri = new CustomRecipeIngredient();
            cri.setRecipe(recipe);
            cri.setIngredient(ing);
            cri.setQuantityGram(item.getQuantityGram());
            cri.setNote(item.getNote());
            ingredients.add(cri);
        }

        // Làm tròn
        recipe.setTotalCalories(Math.round(totalCal * 10)/10.0);
        recipe.setTotalProtein(Math.round(totalPro *10)/10.0);
        recipe.setTotalFat(Math.round(totalFat *10)/10.0);
        recipe.setTotalSaturatedFat(Math.round(totalSatFat *10)/10.0);
        recipe.setTotalCarbs(Math.round(totalCarbs *10)/10.0);
        recipe.setTotalSugar(Math.round(totalSugar *10)/10.0);
        recipe.setTotalFiber(Math.round(totalFiber *10)/10.0);
        recipe.setTotalSodium(Math.round(totalSodium *10)/10.0);
        recipe.setTotalPotassium(Math.round(totalPot *10)/10.0);
        recipe.setTotalCholesterol(Math.round(totalChol *10)/10.0);

        // Lưu recipe trước
        CustomRecipe saved = recipeRepo.save(recipe);
        ingredients.forEach(i -> i.setRecipe(saved));
        recipeIngredientRepo.saveAll(ingredients);

        return saved;
    }

    public MealHistory eatCustomRecipe(String userId, Long recipeId, String mealTypeInput) {
        CustomRecipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức ID: " + recipeId));

        MealHistory meal = new MealHistory();
        meal.setUserId(userId);
        meal.setFoodName(recipe.getName() + " (tự làm)");
        meal.setCalories(recipe.getTotalCalories());
        meal.setProtein(recipe.getTotalProtein());
        meal.setFat(recipe.getTotalFat());
        meal.setCarbs(recipe.getTotalCarbs());
        meal.setSugar(recipe.getTotalSugar());
        meal.setSodium(recipe.getTotalSodium());
        meal.setRecipeId(recipe.getId());
        meal.setMealDate(LocalDate.now());

        String finalMealType = "SNACK"; // default
        if (mealTypeInput != null && !mealTypeInput.isBlank()) {
            finalMealType = mealTypeInput.toUpperCase();
        } else {
            int hour = LocalDateTime.now().getHour();
            finalMealType = switch (hour) {
                case 5, 6, 7, 8, 9, 10 -> "BREAKFAST";
                case 11, 12, 13        -> "LUNCH";
                case 17, 18, 19, 20     -> "DINNER";
                default                -> "SNACK";
            };
        }

        meal.setMealType(MealType.valueOf(finalMealType));

        return mealHistoryRepo.save(meal);
    }

    public List<CustomRecipe> getPublicRecipes() {
        return recipeRepo.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    public List<CustomRecipe> getMyRecipes(String userId) {
        return recipeRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public CustomRecipeDetail getRecipeDetail(Long recipeId) {
        CustomRecipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức"));

        List<CustomRecipeIngredient> ings = recipeIngredientRepo.findByRecipeId(recipeId);

        CustomRecipeDetail detail = new CustomRecipeDetail();
        detail.setRecipe(recipe);

        List<CustomRecipeDetail.IngredientDetail> list = ings.stream().map(cri -> {
            CustomRecipeDetail.IngredientDetail d = new CustomRecipeDetail.IngredientDetail();
            d.setIngredientName(cri.getIngredient().getName());
            d.setEnglishName(cri.getIngredient().getEnglishName());
            d.setQuantityGram(cri.getQuantityGram());
            d.setNote(cri.getNote());
            d.setImageUrl(cri.getIngredient().getImageUrl());
            return d;
        }).toList();

        detail.setIngredients(list);
        return detail;
    }
}