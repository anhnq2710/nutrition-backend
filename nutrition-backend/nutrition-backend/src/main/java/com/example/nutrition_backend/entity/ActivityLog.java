package com.example.nutrition_backend.entity;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(generator = "UUID")

    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name; // e.g. "Running", "Cycling"

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "calories_burned", precision = 10, scale = 2)
    private java.math.BigDecimal caloriesBurned;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
