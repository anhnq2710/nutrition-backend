package com.example.nutrition_backend.entity;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "food_logs", indexes = {
        @Index(name = "idx_food_logs_user_logged_at", columnList = "user_id, logged_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodLog {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @Column(name = "custom_name")
    private String customName;

    @Column(precision = 9, scale = 3)
    private BigDecimal quantity = BigDecimal.ONE;

    private String unit;

    @Column(precision = 10, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 10, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 10, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 10, scale = 2)
    private BigDecimal fatG;

    @Column(name = "logged_at", nullable = false)
    private OffsetDateTime loggedAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
