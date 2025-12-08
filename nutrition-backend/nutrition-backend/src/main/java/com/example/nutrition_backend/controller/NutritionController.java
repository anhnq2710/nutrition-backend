package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    @GetMapping("/nutrition/search")
    public ResponseEntity<List<FoodEntity>> search(@RequestParam String name,
                                                   @RequestParam(required = false, defaultValue = "false") boolean addToHistory,
                                                   @RequestParam(required = false) String userId) {
        List<FoodEntity> results = nutritionService.searchNutrition(name, addToHistory, userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/nutrition/{name}")
    public ResponseEntity<FoodEntity> getByName(@PathVariable String name) {
        Optional<FoodEntity> food = nutritionService.getNutritionByName(name);
        return food.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}