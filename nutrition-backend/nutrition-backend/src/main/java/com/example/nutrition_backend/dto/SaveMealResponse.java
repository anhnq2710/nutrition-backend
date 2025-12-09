package com.example.nutrition_backend.dto;

import com.example.nutrition_backend.entity.MealHistory;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SaveMealResponse {
    private MealHistory savedMeal;

    // Tổng trong ngày (bao gồm bữa vừa lưu)
    private Map<String, Double> dailyTotals; // { totalCalories, totalSugar, totalSodium, totalFat }

    // Thresholds dùng để so sánh (period = 1 day)
    private Map<String, Double> thresholds; // { calorieLimit, sugarLimit, sodiumLimit, fatLimit }

    // per-metric severity: 0/1/2
    private Map<String, Integer> perMetricSeverity;

    // overall warning level
    private int warningLevel;

    private List<String> warnings;
}
