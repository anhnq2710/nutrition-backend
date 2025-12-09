package com.example.nutrition_backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MealHistoryRequest {
    private String userId;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double sodium;

    // optional: nếu null thì mặc định LocalDate.now()
    private LocalDate mealDate;

    // Nếu client gửi string, bạn có thể gửi "BREAKFAST","LUNCH","DINNER","SNACK"
    private MealType mealType;
}
