package com.example.nutrition_backend.dto;

import lombok.Data;

@Data
public class MealDayDTO {
    private String dayName;
    private Long breakfastId;
    private Long lunchId;
    private Long dinnerId;
}