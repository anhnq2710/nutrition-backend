package com.example.nutrition_backend.service;

import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MealHistoryService {

    @Autowired
    private MealHistoryRepository mealRepo;

    public MealHistory saveMeal(String userId, String foodName, Double calories, Double protein, Double fat, Double carbs, Double sugar, Double sodium) {
        MealHistory meal = new MealHistory();
        meal.setUserId(userId);
        meal.setFoodName(foodName);
        meal.setCalories(calories);
        meal.setProtein(protein);
        meal.setFat(fat);
        meal.setCarbs(carbs);
        meal.setSugar(sugar);
        meal.setSodium(sodium);

        return mealRepo.save(meal);
    }

    public List<MealHistory> getHistoryByUser(String userId) {
        return mealRepo.findByUserId(userId);
    }
}
