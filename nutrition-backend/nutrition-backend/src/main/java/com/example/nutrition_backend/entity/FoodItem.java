package com.example.nutrition_backend.entity;

import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "food_items", indexes = {
        @Index(name = "idx_food_items_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String brand;

    // serving description, e.g. "1 cup (240 ml)" or "1 slice"
    @Column(name = "serving_description")
    private String servingDescription;

    // numeric serving amount and unit stored as text separately (e.g. 100, "g")
    @Column(name = "serving_size_amount")
    private BigDecimal servingSizeAmount;

    @Column(name = "serving_size_unit")
    private String servingSizeUnit;

    // nutrition per serving
    @Column(precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatG;

    // optionally per 100g values
    @Column(name = "calories_per_100g", precision = 10, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "source") // e.g. "USDA", "user"
    private String source;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
