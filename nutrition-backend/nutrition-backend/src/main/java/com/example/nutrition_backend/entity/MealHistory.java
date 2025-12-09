package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_history")
@Data
public class MealHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "food_name", length = 255, nullable = false)
    private String foodName;

    @Column(name = "calories", columnDefinition = "DECIMAL(8,2)")
    private Double calories;

    @Column(name = "protein", columnDefinition = "DECIMAL(6,2)")
    private Double protein;

    @Column(name = "fat", columnDefinition = "DECIMAL(6,2)")
    private Double fat;

    @Column(name = "carbs", columnDefinition = "DECIMAL(6,2)")
    private Double carbs;

    @Column(name = "sugar", columnDefinition = "DECIMAL(6,2)")
    private Double sugar;

    @Column(name = "sodium", columnDefinition = "DECIMAL(8,2)")
    private Double sodium;

    @Column(name = "meal_date")
    private LocalDate mealDate = LocalDate.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}