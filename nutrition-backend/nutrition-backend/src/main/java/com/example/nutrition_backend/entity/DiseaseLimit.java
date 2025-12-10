package com.example.nutrition_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "disease_limits")
@Data
public class DiseaseLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "disease_name", nullable = false, unique = true, length = 100)
    private String diseaseName;  // "diabetes", "hypertension", "cardiovascular"

    @Column(name = "viet_name", nullable = false, length = 150)
    private String vietName;     // "Tiểu đường", "Tăng huyết áp", "Bệnh tim mạch"

    @Column(name = "sugar_max", columnDefinition = "DOUBLE PRECISION")
    private Double sugarMax;     // g/ngày

    @Column(name = "sodium_max", columnDefinition = "DOUBLE PRECISION")
    private Double sodiumMax;    // mg/ngày

    @Column(name = "fat_max", columnDefinition = "DOUBLE PRECISION")
    private Double fatMax;       // g/ngày

    @Column(name = "calorie_max", columnDefinition = "DOUBLE PRECISION")
    private Double calorieMax;   // kcal/ngày

    @Column(name = "note", length = 500)
    private String note;         // Gợi ý: "Tránh chè, nước ngọt", "Hạn chế muối..."

    // Để JPA map ngược từ HealthProfile (nếu cần)
    // @OneToMany(mappedBy = "disease", fetch = FetchType.LAZY)
    // private List<HealthProfile> profiles;
}