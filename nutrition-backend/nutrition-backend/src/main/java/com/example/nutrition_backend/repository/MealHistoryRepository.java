package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.MealHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MealHistoryRepository extends JpaRepository<MealHistory, Long> {
    List<MealHistory> findByUserIdAndMealDateBetween(String userId, LocalDate start, LocalDate end);
    List<MealHistory> findByUserId(String userId);
    List<MealHistory> findByUserIdOrderByMealDateDescCreatedAtDesc(String userId);
    List<MealHistory> findByUserIdAndMealDateBetweenOrderByMealDateDescCreatedAtDesc(String userId, LocalDate from, LocalDate to);
    // method needed for daily totals
    List<MealHistory> findByUserIdAndMealDate(String userId, LocalDate mealDate);

}