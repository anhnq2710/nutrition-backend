// src/main/java/com/example/nutrition_backend/dto/StatisticsAdvice.java
package com.example.nutrition_backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsAdvice {
    private String period;
    private String message;

    private int totalMeals;

    // Các chỉ số trung bình và tổng cho toàn kỳ
//    private double totalCalories;
//    private double avgCaloriesPerMeal;
//    private double caloriesPerDay;
//
//    private double totalProtein;
//    private double avgProteinPerMeal;
//    private double proteinPerDay;
//
//    private double totalFat;
//    private double avgFatPerMeal;
//    private double fatPerDay;
//
//    private double totalSaturatedFat;
//    private double avgSaturatedFatPerMeal;
//    private double saturatedFatPerDay;
//
//    private double totalCarbs;
//    private double avgCarbsPerMeal;
//    private double carbsPerDay;
//
//    private double totalSugar;
//    private double avgSugarPerMeal;
//    private double sugarPerDay;
//
//    private double totalFiber;
//    private double avgFiberPerMeal;
//    private double fiberPerDay;
//
//    private double totalSodium;
//    private double avgSodiumPerMeal;
//    private double sodiumPerDay;
//
//    private double totalPotassium;
//    private double avgPotassiumPerMeal;
//    private double potassiumPerDay;
//
//    private double totalCholesterol;
//    private double avgCholesterolPerMeal;
//    private double cholesterolPerDay;

    // Các thông tin bổ sung
    private Map<String, Object> fullIndices;        // Tất cả chỉ số chi tiết
    private Map<String, Object> thresholds;         // Giới hạn theo bệnh lý/kỳ
    private Map<String, Integer> perMetricSeverity; // Mức độ cảnh báo từng chỉ số
    private int warningLevel;                       // 0/1/2
    private List<String> warnings;
    private List<String> suggestions;
}