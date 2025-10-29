package com.example.nutrition_backend.entity;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "meal_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealItem {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem; // optional if custom

    @Column(name = "custom_name")
    private String customName;

    @Column(precision = 9, scale = 3)
    private BigDecimal quantity = BigDecimal.ONE;

    private String unit; // "g", "cup", "slice", etc.

    @Column(precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatG;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
