
package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/user-meals")
public class UserMealHistoryAdminController {

    @Autowired
    private MealHistoryRepository mealRepo;

    // Xem lịch sử ăn uống của 1 user theo userId
    @GetMapping("/{userId}")
    public ResponseEntity<List<MealHistory>> getMealHistoryByUserId(@PathVariable String userId) {
        List<MealHistory> meals = mealRepo.findByUserIdOrderByMealDateDesc(userId);

        if (meals.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Trả mảng rỗng nếu chưa ăn gì
        }

        return ResponseEntity.ok(meals);
    }

    // (Tùy chọn) Xem lịch sử trong khoảng thời gian
    @GetMapping("/{userId}/range")
    public ResponseEntity<List<MealHistory>> getMealHistoryInRange(
            @PathVariable String userId,
            @RequestParam String startDate, // YYYY-MM-DD hoặc YYYY-M-D
            @RequestParam String endDate) {

        try {
            // Tự động thêm 0 nếu ngày/tháng thiếu
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("[yyyy-MM-dd][yyyy-M-d]"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("[yyyy-MM-dd][yyyy-M-d]"));

            List<MealHistory> meals = mealRepo.findByUserIdAndMealDateBetweenOrderByMealDateDesc(
                    userId, start, end);

            return ResponseEntity.ok(meals);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(List.of()); //
        }
    }
}