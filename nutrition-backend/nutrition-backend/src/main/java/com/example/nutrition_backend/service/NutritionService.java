package com.example.nutrition_backend.service;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.FoodRepository;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NutritionService {

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private MealHistoryRepository mealRepo;

    public List<FoodEntity> searchNutrition(String name, boolean addToHistory, String userId) {
        List<FoodEntity> results = foodRepo.searchByName(name);

        // Nếu có kết quả và addToHistory = true, lưu vào history (lấy món đầu tiên)
        if (!results.isEmpty() && addToHistory && userId != null) {
            FoodEntity food = results.get(0);
            MealHistory meal = new MealHistory();
            meal.setUserId(userId);
            meal.setFoodName(food.getName());
            meal.setCalories(food.getCalories());
            meal.setProtein(food.getProtein());
            meal.setFat(food.getFat());
            meal.setCarbs(food.getCarbs());
            meal.setSugar(food.getSugar());
            meal.setSodium(food.getSodium());
            mealRepo.save(meal);
        }

        return results;
    }

    public Optional<FoodEntity> getNutritionByName(String name) {
        List<FoodEntity> results = foodRepo.searchByName(name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}