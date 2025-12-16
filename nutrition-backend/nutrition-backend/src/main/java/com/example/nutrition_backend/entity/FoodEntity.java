package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "food_nutrition")
@Data
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "english_name", length = 255)
    private String englishName;

    // TẤT CẢ CHỈ SỐ DINH DƯỠNG BÂY GIỜ LÀ CHO 100G (khi is_per_100g = true)
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

    @Column(name = "serving_size", length = 100)
    private String servingSize; // Ví dụ: "1 tô (350g)", "100g", "1 thìa (5g)"

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ==================== 2 CỘT MỚI – SIÊU QUAN TRỌNG ====================
    @Column(name = "is_per_100g")
    private boolean isPer100g = true; // TRUE = chỉ số trên là cho 100g (mặc định)

    @Column(name = "serving_multiplier", columnDefinition = "DECIMAL(6,2)")
    private Double servingMultiplier = 1.0; // Hệ số nhân để ra serving thực tế
    // Ví dụ: serving 350g → multiplier = 3.5
    // Gia vị (muối, đường) → multiplier = 1.0 (vì đã là 100g)
}