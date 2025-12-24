package com.example.nutrition_backend.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class MealDay {
    private String dayName; // "Thứ Hai", "Thứ Ba"...
    private Long breakfastId; // ID món sáng
    private Long lunchId;      // ID món trưa
    private Long dinnerId;     // ID món tối
}
