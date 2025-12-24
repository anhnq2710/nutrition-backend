package com.example.nutrition_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WeeklyMealPlanRequest {
    private String userId;
    private LocalDate startDate; // Thứ 2 của tuần
    private List<MealDayDTO> days;
    // getters/setters
}
