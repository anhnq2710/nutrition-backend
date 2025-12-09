package com.example.nutrition_backend.service;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.FoodRepository;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NutritionService {

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private MealHistoryRepository mealRepo;

    // Giả sử bạn có NutritionAdvisorService để lấy lời khuyên
    @Autowired
    private NutritionAdvisorService advisorService;  // Import nếu chưa có

    public Map<String, Object> searchNutrition(String name, boolean addToHistory, String userId) {
        List<FoodEntity> results = foodRepo.searchByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("foods", results);

        if (results.isEmpty()) {
            response.put("message", "Không tìm thấy món ăn!");
            return response;
        }

        // Tự động lưu vào lịch sử nếu addToHistory = true
        if (addToHistory && userId != null) {
            FoodEntity food = results.get(0);  // Lấy món đầu tiên
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
            response.put("savedToHistory", true);
        }

        // Thêm lời khuyên cá nhân hóa nếu có userId
        if (userId != null) {
            FoodEntity food = results.get(0);
            Map<String, Object> advice = advisorService.getAdviceForFood(userId, food);  // Gọi service lời khuyên
            response.put("advice", advice);
        }

        return response;
    }

    public Optional<FoodEntity> getNutritionByName(String name) {
        List<FoodEntity> results = foodRepo.searchByName(name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}