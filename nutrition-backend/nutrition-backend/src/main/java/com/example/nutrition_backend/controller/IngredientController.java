// src/main/java/com/example/nutrition_backend/controller/IngredientController.java
package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    // 1. Lấy tất cả nguyên liệu (dùng khi tạo món)
    @GetMapping
    public ResponseEntity<List<FoodEntity>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    // 2. Tìm nguyên liệu theo tên (search box trong FE)
    @GetMapping("/search")
    public ResponseEntity<List<FoodEntity>> searchIngredients(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(ingredientService.searchIngredients(name));
    }

    // 3. Lấy chi tiết 1 nguyên liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<FoodEntity> getIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }
}