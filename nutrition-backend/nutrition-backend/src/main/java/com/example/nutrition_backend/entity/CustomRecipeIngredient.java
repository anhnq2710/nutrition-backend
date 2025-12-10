package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "custom_recipe_ingredients")
@Data
public class CustomRecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private CustomRecipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "quantity_gram", nullable = false)
    private Double quantityGram;

    @Column(name = "note", length = 255)
    private String note;
}