package com.example.nutrition_backend.controller;


import com.example.nutrition_backend.dto.MealHistoryRequest;
import com.example.nutrition_backend.dto.SaveMealResponse;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.service.MealHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HistoryController {

    @Autowired
    private MealHistoryService mealService;

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

}