package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.dto.WeightGoal;
import com.example.nutrition_backend.repository.FoodRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardAdminController {

    @Autowired
    private HealthProfileRepository profileRepo;

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private MealHistoryRepository mealRepo;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Tổng số user có profile
        long totalUsers = profileRepo.count();
        stats.put("totalUsers", totalUsers);

        // Tổng số món ăn cơ bản (food_nutrition)
        long totalFoods = foodRepo.count();
        stats.put("totalFoods", totalFoods);

        // Tổng số bữa ăn đã lưu
        long totalMeals = mealRepo.count();
        stats.put("totalMeals", totalMeals);

        // Thống kê bệnh lý
        long diabetesCount = profileRepo.countByHasDiabetesTrue();
        long hypertensionCount = profileRepo.countByHasHypertensionTrue();
        long cardiovascularCount = profileRepo.countByHasCardiovascularTrue();

        Map<String, Long> diseaseStats = new HashMap<>();
        diseaseStats.put("diabetes", diabetesCount);
        diseaseStats.put("hypertension", hypertensionCount);
        diseaseStats.put("cardiovascular", cardiovascularCount);
        stats.put("diseaseStats", diseaseStats);

        // Mục tiêu cân nặng
        long loseWeightCount = profileRepo.countByWeightGoal(WeightGoal.LOSE);
        long gainWeightCount = profileRepo.countByWeightGoal(WeightGoal.GAIN);
        long maintainWeightCount = profileRepo.countByWeightGoal(WeightGoal.MAINTAIN) + profileRepo.countByWeightGoalIsNull();

        Map<String, Long> weightGoalStats = new HashMap<>();
        weightGoalStats.put("lose", loseWeightCount);
        weightGoalStats.put("gain", gainWeightCount);
        weightGoalStats.put("maintain", maintainWeightCount);
        stats.put("weightGoalStats", weightGoalStats);

        // alo trung bình/ngày (7 ngày gần nhất)
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);
        List<Object[]> avgCaloriePerDay = mealRepo.findAvgCaloriesPerDay(weekAgo, today);

        List<Map<String, Object>> calorieTrend = new ArrayList<>();
        for (Object[] row : avgCaloriePerDay) {
            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", row[0]);
            dayStat.put("avgCalories", row[1]);
            calorieTrend.add(dayStat);
        }
        stats.put("calorieTrendLast7Days", calorieTrend);

        // Top 10 món ăn được ăn nhiều nhất
        List<Object[]> topFoods = mealRepo.findTopFoods(10);
        List<Map<String, Object>> topFoodList = new ArrayList<>();
        for (Object[] row : topFoods) {
            Map<String, Object> foodStat = new HashMap<>();
            foodStat.put("foodName", row[0]);
            foodStat.put("timesEaten", row[1]);
            topFoodList.add(foodStat);
        }
        stats.put("top10Foods", topFoodList);

        // Thông tin hệ thống
        stats.put("lastUpdated", LocalDate.now().toString());
        stats.put("message", "Dashboard thống kê hệ thống dinh dưỡng");

        return ResponseEntity.ok(stats);
    }
}