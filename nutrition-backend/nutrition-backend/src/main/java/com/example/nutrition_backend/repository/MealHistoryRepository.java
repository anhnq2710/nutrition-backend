package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.MealHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealHistoryRepository extends JpaRepository<MealHistory, Long> {
    List<MealHistory> findByUserIdAndMealDateBetween(String userId, LocalDate start, LocalDate end);
    List<MealHistory> findByUserId(String userId);
    List<MealHistory> findByUserIdOrderByMealDateDescCreatedAtDesc(String userId);
    List<MealHistory> findByUserIdAndMealDateBetweenOrderByMealDateDescCreatedAtDesc(String userId, LocalDate from, LocalDate to);
    // method needed for daily totals
    List<MealHistory> findByUserIdAndMealDate(String userId, LocalDate mealDate);
    @Query("SELECT m.mealDate, AVG(m.calories) FROM MealHistory m WHERE m.mealDate BETWEEN :start AND :end GROUP BY m.mealDate ORDER BY m.mealDate")
    List<Object[]> findAvgCaloriesPerDay(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Top món ăn
    @Query("SELECT m.foodName, COUNT(m) FROM MealHistory m GROUP BY m.foodName ORDER BY COUNT(m) DESC")
    List<Object[]> findTopFoods(int limit);
    // Lấy lịch sử của 1 user, sắp xếp mới nhất trước
    List<MealHistory> findByUserIdOrderByMealDateDesc(String userId);

    // Lấy trong khoảng thời gian
    List<MealHistory> findByUserIdAndMealDateBetweenOrderByMealDateDesc(
            String userId, LocalDate startDate, LocalDate endDate);
}