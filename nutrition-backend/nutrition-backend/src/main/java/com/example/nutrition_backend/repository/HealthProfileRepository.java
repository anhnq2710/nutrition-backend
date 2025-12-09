package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
    Optional<HealthProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);
}