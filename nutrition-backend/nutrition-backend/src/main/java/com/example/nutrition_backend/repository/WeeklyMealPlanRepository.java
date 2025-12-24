package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.WeeklyMealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyMealPlanRepository extends JpaRepository<WeeklyMealPlan, Long> {
    List<WeeklyMealPlan> findByUserId(String userId);
    Optional<WeeklyMealPlan> findByUserIdAndStartDate(String userId, LocalDate startDate);
}
