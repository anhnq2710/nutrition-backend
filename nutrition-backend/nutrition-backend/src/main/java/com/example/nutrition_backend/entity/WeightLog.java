package com.example.nutrition_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;


@Entity
@Table(name = "weight_logs", indexes = {
        @Index(name = "idx_weight_user_time", columnList = "user_id, recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeightLog {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
