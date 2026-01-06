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
import java.util.stream.Collectors;

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

    // HistoryController.java – SỬA METHOD XÓA MÓN (HỖ TRỢ XÓA NHIỀU ID CÙNG LÚC)

    @DeleteMapping("/history")
    public ResponseEntity<?> deleteMeals(
            @RequestBody Map<String, Object> request) {

        String userId = (String) request.get("userId");
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId là bắt buộc"));
        }

        @SuppressWarnings("unchecked")
        Object idsObj = request.get("ids");

        if (idsObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Danh sách ids là bắt buộc"));
        }

        List<Long> ids;
        if (idsObj instanceof List) {
            ids = ((List<?>) idsObj).stream()
                    .filter(obj -> obj instanceof Number)
                    .map(obj -> ((Number) obj).longValue())
                    .collect(Collectors.toList());
        } else if (idsObj instanceof Number) {
            // Hỗ trợ xóa 1 món
            ids = List.of(((Number) idsObj).longValue());
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "ids phải là số hoặc danh sách số"));
        }

        if (ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Danh sách ids rỗng"));
        }

        mealService.deleteMultipleMeals(userId, ids);

        return ResponseEntity.ok(Map.of(
                "message", "Xóa thành công " + ids.size() + " món khỏi lịch sử!"
        ));
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