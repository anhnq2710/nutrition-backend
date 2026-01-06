
package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.dto.WeeklyMealPlanRequest;
import com.example.nutrition_backend.entity.WeeklyMealPlan;
import com.example.nutrition_backend.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition/meal-plan")
public class MealPlanController {

    @Autowired
    private NutritionService nutritionService;

    // POST: Tạo/lưu kế hoạch tuần
    @PostMapping("/week")
    public ResponseEntity<WeeklyMealPlan> saveWeeklyPlan(@RequestBody WeeklyMealPlanRequest request) {
        WeeklyMealPlan plan = nutritionService.saveWeeklyMealPlan(request);
        return ResponseEntity.ok(plan);
    }

    // GET: Lấy kế hoạch tuần hiện tại của user
    @GetMapping("/week/current")
    public ResponseEntity<WeeklyMealPlan> getCurrentWeekPlan(@RequestParam String userId) {
        WeeklyMealPlan plan = nutritionService.getCurrentWeekPlan(userId);
        return ResponseEntity.ok(plan);
    }
}