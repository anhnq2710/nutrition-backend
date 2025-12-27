package com.example.nutrition_backend.controller;


import com.example.nutrition_backend.dto.MealHistoryRequest;
import com.example.nutrition_backend.dto.SaveMealResponse;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import com.example.nutrition_backend.service.MealHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HistoryController {

    @Autowired
    private MealHistoryService mealService;

    @Autowired
    private MealHistoryRepository mealHistoryRepository;

    @PostMapping("/history/save")
    public ResponseEntity<?> save(@RequestBody MealHistoryRequest req) {
        try {
            SaveMealResponse resp = mealService.saveMealWithWarning(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Save failed"));
        }
    }

    @GetMapping("/history/{userId}")
    public List<MealHistory> getHistory(
            @PathVariable String userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
        return mealService.getHistoryByUserAndDateRange(userId, start, end);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteSingleMeal(
            @PathVariable Long id,
            @RequestParam String userId) {

        Optional<MealHistory> mealOpt = mealHistoryRepository.findById(id);
        if (mealOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MealHistory meal = mealOpt.get();
        if (!meal.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền xóa món này"));
        }

        mealHistoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa món ăn khỏi lịch sử thành công!"));
    }

    @DeleteMapping("/history/meal")
    public ResponseEntity<?> deleteEntireMeal(
            @RequestParam String userId,
            @RequestParam String mealDate,
            @RequestParam String mealType) {

        LocalDate date;
        try {
            date = LocalDate.parse(mealDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng ngày sai (YYYY-MM-DD)"));
        }

        mealService.deleteEntireMeal(userId, date, mealType);

        return ResponseEntity.ok(Map.of(
                "message", "Xóa toàn bộ bữa " + mealType + " ngày " + mealDate + " thành công!"
        ));
    }
}