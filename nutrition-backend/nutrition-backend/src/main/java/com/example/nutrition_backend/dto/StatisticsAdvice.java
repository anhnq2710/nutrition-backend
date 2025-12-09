package com.example.nutrition_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsAdvice {
    private String period;  // "Từ 2025-12-01 đến 2025-12-09"
    private Double avgCalories;
    private Double totalCalories;
    private Double avgSugar;
    private Double totalSugar;
    private Double avgSodium;
    private Double totalSodium;
    private int totalMeals;
    private List<String> warnings;  // "Đường vượt 150%..."
    private List<String> suggestions;  // "Giảm chè..."
    private String message;  // Nếu không có dữ liệu
}