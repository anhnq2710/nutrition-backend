package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ingredients")
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "english_name", length = 255)
    private String englishName;

    @Column(name = "calories", columnDefinition = "DOUBLE PRECISION")
    private Double calories;

    @Column(name = "protein", columnDefinition = "DOUBLE PRECISION")
    private Double protein;

    @Column(name = "fat", columnDefinition = "DOUBLE PRECISION")
    private Double fat;

    @Column(name = "saturated_fat", columnDefinition = "DOUBLE PRECISION")
    private Double saturatedFat;

    @Column(name = "carbs", columnDefinition = "DOUBLE PRECISION")
    private Double carbs;

    @Column(name = "sugar", columnDefinition = "DOUBLE PRECISION")
    private Double sugar;

    @Column(name = "fiber", columnDefinition = "DOUBLE PRECISION")
    private Double fiber;

    @Column(name = "sodium", columnDefinition = "DOUBLE PRECISION")
    private Double sodium;

    @Column(name = "potassium", columnDefinition = "DOUBLE PRECISION")
    private Double potassium;

    @Column(name = "cholesterol", columnDefinition = "DOUBLE PRECISION")
    private Double cholesterol;

    @Column(name = "serving_size", length = 100)
    private String servingSize;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}