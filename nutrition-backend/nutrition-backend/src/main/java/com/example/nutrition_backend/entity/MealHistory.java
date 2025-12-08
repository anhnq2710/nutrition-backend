package com.example.nutrition_backend.entity;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_history")
@Data
public class MealHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double sodium;
    private LocalDate mealDate = LocalDate.now();
    private LocalDateTime createdAt = LocalDateTime.now();
}