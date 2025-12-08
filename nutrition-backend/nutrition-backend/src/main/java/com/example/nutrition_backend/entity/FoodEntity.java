package com.example.nutrition_backend.entity;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "food_nutrition")
@Data
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // "phở bò"
    private String englishName;  // "beef pho"

    // Dinh dưỡng (per serving 100g)
    private Double calories;
    private Double protein;
    private Double fat;
    private Double saturatedFat;
    private Double carbs;
    private Double sugar;
    private Double fiber;
    private Double sodium;
    private Double potassium;
    private Double cholesterol;

    private String servingSize;  // "1 tô (300g)"
    private String note;  // "Phù hợp tiểu đường"
}