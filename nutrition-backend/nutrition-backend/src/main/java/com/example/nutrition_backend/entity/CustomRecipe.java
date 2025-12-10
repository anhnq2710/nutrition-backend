package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_recipes")
@Data
public class CustomRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "servings")
    private Integer servings = 1;

    // Tổng dinh dưỡng (tính tự động)
    @Column(name = "total_calories", columnDefinition = "DOUBLE PRECISION")
    private Double totalCalories;

    @Column(name = "total_protein", columnDefinition = "DOUBLE PRECISION")
    private Double totalProtein;

    @Column(name = "total_fat", columnDefinition = "DOUBLE PRECISION")
    private Double totalFat;

    @Column(name = "total_saturated_fat", columnDefinition = "DOUBLE PRECISION")
    private Double totalSaturatedFat;

    @Column(name = "total_carbs", columnDefinition = "DOUBLE PRECISION")
    private Double totalCarbs;

    @Column(name = "total_sugar", columnDefinition = "DOUBLE PRECISION")
    private Double totalSugar;

    @Column(name = "total_fiber", columnDefinition = "DOUBLE PRECISION")
    private Double totalFiber;

    @Column(name = "total_sodium", columnDefinition = "DOUBLE PRECISION")
    private Double totalSodium;

    @Column(name = "total_potassium", columnDefinition = "DOUBLE PRECISION")
    private Double totalPotassium;

    @Column(name = "total_cholesterol", columnDefinition = "DOUBLE PRECISION")
    private Double totalCholesterol;

    @Column(name = "is_public")
    private boolean isPublic = true;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}