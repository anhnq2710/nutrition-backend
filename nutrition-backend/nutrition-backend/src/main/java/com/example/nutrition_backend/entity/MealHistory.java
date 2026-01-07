package com.example.nutrition_backend.entity;

import com.example.nutrition_backend.dto.MealType;
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
    private Long id;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "food_name", length = 255, nullable = false)
    private String foodName;

    // Các chỉ số dinh dưỡng (per serving thực tế)
    @Column(name = "calories", columnDefinition = "DECIMAL(8,2)")
    private Double calories;

    @Column(name = "protein", columnDefinition = "DECIMAL(6,2)")
    private Double protein;

    @Column(name = "fat", columnDefinition = "DECIMAL(6,2)")
    private Double fat;

    @Column(name = "saturated_fat", columnDefinition = "DECIMAL(6,2)")
    private Double saturatedFat;

    @Column(name = "carbs", columnDefinition = "DECIMAL(6,2)")
    private Double carbs;

    @Column(name = "sugar", columnDefinition = "DECIMAL(6,2)")
    private Double sugar;

    @Column(name = "fiber", columnDefinition = "DECIMAL(6,2)")
    private Double fiber;

    @Column(name = "sodium", columnDefinition = "DECIMAL(8,2)")
    private Double sodium;

    @Column(name = "potassium", columnDefinition = "DECIMAL(8,2)")
    private Double potassium;

    @Column(name = "cholesterol", columnDefinition = "DECIMAL(8,2)")
    private Double cholesterol;

    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", length = 20, nullable = false)
    private MealType mealType; // Enum: BREAKFAST, LUNCH, DINNER, SNACK

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "recipe_id")
    private Long recipeId;
}