package com.example.nutrition_backend.entity;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "health_profile")
@Data
public class HealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private boolean hasDiabetes = false;
    private Double hba1c;
    private boolean hasHypertension = false;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private boolean hasCardiovascular = false;
    private Double cholesterolTotal;
    private Double weightKg;
    private Integer heightCm;
    private Integer age;
    private String gender;

    public double getDailyCalorieLimit() {
        if (weightKg == null || age == null || heightCm == null) return 2000.0;
        double bmr = 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age); // Nam
        if ("female".equalsIgnoreCase(gender)) bmr = 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        double limit = bmr * 1.55; // Moderate
        if (hasDiabetes) limit *= 0.9;
        if (hasHypertension) limit *= 0.95;
        if (hasCardiovascular) limit *= 0.9;
        return limit;
    }
}