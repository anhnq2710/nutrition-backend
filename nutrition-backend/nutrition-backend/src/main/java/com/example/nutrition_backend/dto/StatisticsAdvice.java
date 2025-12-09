package com.example.nutrition_backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsAdvice {
    private String period;
    private String message;

    private double avgCalories;
    private double totalCalories;
    private double avgSugar;
    private double totalSugar;
    private double avgSodium;
    private double totalSodium;
    private double avgFat;
    private double totalFat;
    private int totalMeals;

    // NEW fields
    private Map<String, Object> fullIndices;        // totals + per-day + avg per meal
    private Map<String, Object> thresholds;         // threshold for period (e.g. sugarLimitPeriod)
    private Map<String, Integer> perMetricSeverity; // e.g. {"sugar":1, "sodium":0}
    private int warningLevel;                       // 0/1/2
    private List<String> warnings;
    private List<String> suggestions;
}
