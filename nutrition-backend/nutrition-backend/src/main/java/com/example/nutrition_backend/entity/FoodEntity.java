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

    // SỬA: Dùng columnDefinition cho DECIMAL thay precision/scale
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
    private String servingSize;

    @Column(name = "note", length = 255)
    private String note;
}