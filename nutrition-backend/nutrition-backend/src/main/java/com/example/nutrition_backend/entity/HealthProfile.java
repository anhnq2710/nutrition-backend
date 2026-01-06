package com.example.nutrition_backend.entity;

import com.example.nutrition_backend.dto.WeightGoal;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "health_profile")
@Data
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 255)
    private String userId;

    @Column(name = "has_diabetes")
    private boolean hasDiabetes = false;

    @Column(name = "hba1c", columnDefinition = "DOUBLE PRECISION")
    private Double hba1c;

    @Column(name = "has_hypertension")
    private boolean hasHypertension = false;

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    @Column(name = "has_cardiovascular")
    private boolean hasCardiovascular = false;

    @Column(name = "cholesterol_total", columnDefinition = "DOUBLE PRECISION")
    private Double cholesterolTotal;

    @Column(name = "weight_kg", columnDefinition = "DOUBLE PRECISION")
    private Double weightKg;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", length = 20)
    private String gender;

    // Foreign key đến bảng disease_limits
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = true)
    private DiseaseLimit
            disease;

    @Column(name = "weight_goal", length = 20)
    @Enumerated(EnumType.STRING)
    private WeightGoal weightGoal; // "LOSE" (giảm), "GAIN" (tăng), "MAINTAIN" (giữ), null = tự động theo BMI

    public void setWeightGoal(WeightGoal weightGoal) {
        this.weightGoal = (weightGoal == null) ? WeightGoal.MAINTAIN : weightGoal;
    }

    // Tính giới hạn calo hàng ngày
    public double getDailyCalorieLimit() {
        if (weightKg == null || age == null || heightCm == null) {
            return 2000.0;
        }

        double bmr;
        if ("female".equalsIgnoreCase(gender)) {
            bmr = 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        } else {
            bmr = 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
        }

        double limit = bmr * 1.55; // Hoạt động trung bình

        if (hasDiabetes) limit *= 0.9;
        if (hasHypertension) limit *= 0.95;
        if (hasCardiovascular) limit *= 0.9;

        return Math.round(limit * 10) / 10.0; // Làm tròn 1 chữ số
    }
}