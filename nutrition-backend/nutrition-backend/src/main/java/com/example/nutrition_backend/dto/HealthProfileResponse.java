
package com.example.nutrition_backend.dto;

import lombok.Data;

@Data
public class HealthProfileResponse {
    private String userId;
    private boolean hasDiabetes;
    private Double hba1c;
    private boolean hasHypertension;
    private Double bloodPressureSystolic;
    private Double bloodPressureDiastolic;
    private boolean hasCardiovascular;
    private Double weightKg;
    private Double heightCm;
    private Integer age;
    private String gender;
    private Double dailyCalorieLimit;
    private WeightGoal weightGoal;
    // Các field khác

    // Nếu cần disease name
    private String diseaseName;
}