package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.service.FoodAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/foods")
@CrossOrigin(origins = "http://localhost:4200") // Sau này đổi thành domain FE thật
public class FoodAdminController {

    @Autowired
    private FoodAdminService foodAdminService;

    // 1. Lấy danh sách món (cơ bản + custom nếu có)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size);

        Page<FoodEntity> foodPage = foodAdminService.getAllFoods(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("foods", foodPage.getContent()); // danh sách món trang hiện tại
        response.put("currentPage", foodPage.getNumber());
        response.put("totalItems", foodPage.getTotalElements());
        response.put("totalPages", foodPage.getTotalPages());
        response.put("pageSize", foodPage.getSize());

        return ResponseEntity.ok(response);
    }

    // 2. Thêm món mới – calories là per 100g
    @PostMapping
    public ResponseEntity<?> addFood(
            @RequestParam String name,
            @RequestParam(required = false) String englishName,
            @RequestParam Double calories, // per 100g
            @RequestParam Double protein,  // per 100g
            @RequestParam Double fat,
            @RequestParam Double saturatedFat,
            @RequestParam Double carbs,
            @RequestParam Double sugar,
            @RequestParam Double fiber,
            @RequestParam Double sodium,
            @RequestParam Double potassium,
            @RequestParam Double cholesterol,
            @RequestParam String servingSize, // ví dụ "1 tô (350g)"
            @RequestParam(required = false) String note,
            @RequestParam(required = false) MultipartFile image) throws Exception {

        FoodEntity food = foodAdminService.addFoodWithImage(
                name, englishName, calories, protein, fat, saturatedFat,
                carbs, sugar, fiber, sodium, potassium, cholesterol,
                servingSize, note, image);

        return ResponseEntity.ok(buildFoodResponse(food, "Thêm món thành công!"));
    }

    // 3. Sửa món – chỉ cập nhật trường nào gửi lên
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String englishName,
            @RequestParam(required = false) Double calories,
            @RequestParam(required = false) Double protein,
            @RequestParam(required = false) Double fat,
            @RequestParam(required = false) Double saturatedFat,
            @RequestParam(required = false) Double carbs,
            @RequestParam(required = false) Double sugar,
            @RequestParam(required = false) Double fiber,
            @RequestParam(required = false) Double sodium,
            @RequestParam(required = false) Double potassium,
            @RequestParam(required = false) Double cholesterol,
            @RequestParam(required = false) String servingSize,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) MultipartFile image) throws Exception {

        FoodEntity updated = foodAdminService.updateFoodWithImage(
                id, name, englishName, calories, protein, fat, saturatedFat,
                carbs, sugar, fiber, sodium, potassium, cholesterol,
                servingSize, note, image);

        return ResponseEntity.ok(buildFoodResponse(updated, "Cập nhật món thành công!"));
    }

    // 4. Xóa món
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFood(@PathVariable Long id) {
        foodAdminService.deleteFood(id);
        return ResponseEntity.ok(Map.of("message", "Xóa món thành công!"));
    }

    // Helper: Trả JSON sạch + calo thực tế cho serving
    private Map<String, Object> buildFoodResponse(FoodEntity food, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);

        Map<String, Object> foodMap = new HashMap<>();
        foodMap.put("id", food.getId());
        foodMap.put("name", food.getName());
        foodMap.put("englishName", food.getEnglishName());
        foodMap.put("caloriesPer100g", food.getCalories());
        foodMap.put("proteinPer100g", food.getProtein());
        foodMap.put("fatPer100g", food.getFat());
        foodMap.put("carbsPer100g", food.getCarbs());
        foodMap.put("sugarPer100g", food.getSugar());
        foodMap.put("sodiumPer100g", food.getSodium());
        foodMap.put("servingSize", food.getServingSize());
        foodMap.put("imageUrl", food.getImageUrl());

        // Tính calo thực tế cho serving (rất hay khi hiển thị)
        double multiplier = food.getServingMultiplier() != null ? food.getServingMultiplier() : 1.0;
        foodMap.put("actualCalories", Math.round(food.getCalories() * multiplier * 10) / 10.0);

        response.put("food", foodMap);
        return response;
    }
}