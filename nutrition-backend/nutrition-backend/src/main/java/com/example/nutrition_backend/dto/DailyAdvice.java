package com.example.nutrition_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailyAdvice {
    private String date;
    private Double avgCalories;
    private Double calorieLimit;
    private Double avgSugar;
    private Double avgSodium;
    private List<String> warnings;
    private List<String> suggestions;
    private String message;  // Nếu chưa có profile
}