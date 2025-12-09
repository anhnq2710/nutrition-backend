package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    @GetMapping("/nutrition/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "false") boolean suggest,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();

        if (suggest) {
            // Trả phần gợi ý (danh sách món/nghĩa là lightweight search)
            List<Map<String, Object>> suggestions = nutritionService.suggestNutrition(name, limit);
            response.put("suggestions", suggestions);
            response.put("count", suggestions.size());
        } else {
            Map<String, Object> results = nutritionService.searchNutrition(name,userId);
            response.put("results", results);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nutrition/{name}")
    public ResponseEntity<FoodEntity> getByName(@PathVariable String name) {
        Optional<FoodEntity> food = nutritionService.getNutritionByName(name);
        return food.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nutrition/recommend")
    public ResponseEntity<List<Map<String,Object>>> recommendForUser(
            @RequestParam("userId") String userId,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Map<String, Object>> list = nutritionService.recommendForUser(userId, limit);
        return ResponseEntity.ok(list);
    }
}