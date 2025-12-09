package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "disease_limits")
@Data
public class DiseaseLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String diseaseName;  // Tên bệnh

    @Column(nullable = false)
    private String vietName;  // Tên tiếng Việt

    @Column
    private Double sugarMax;  // Ngưỡng đường tối đa (g/ngày)

    @Column
    private Double sodiumMax;  // Ngưỡng natri tối đa (mg/ngày)

    @Column
    private Double fatMax;  // Ngưỡng chất béo tối đa (g/ngày)

    @Column
    private Double calorieMax;  // Ngưỡng calo tối đa (kcal/ngày)

    @Column
    private String note;  //
}