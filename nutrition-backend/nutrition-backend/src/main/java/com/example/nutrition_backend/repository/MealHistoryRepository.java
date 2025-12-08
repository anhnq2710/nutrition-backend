package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.MealHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MealHistoryRepository extends JpaRepository<MealHistory, Long> {
    List<MealHistory> findByUserIdAndMealDateBetween(String userId, LocalDate start, LocalDate end);
    List<MealHistory> findByUserId(String userId);
}