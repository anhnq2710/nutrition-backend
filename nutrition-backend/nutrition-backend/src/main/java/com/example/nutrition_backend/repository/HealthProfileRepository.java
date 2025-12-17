package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.dto.WeightGoal;
import com.example.nutrition_backend.entity.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
    Optional<HealthProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);
    long countByHasDiabetesTrue();

    // Đếm user có bệnh huyết áp
    long countByHasHypertensionTrue();

    // Đếm user có bệnh tim mạch
    long countByHasCardiovascularTrue();

    // Đếm theo mục tiêu cân nặng
    long countByWeightGoal(WeightGoal weightGoal);

    // đếm null
    long countByWeightGoalIsNull();
}