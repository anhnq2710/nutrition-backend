package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.service.FoodAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/foods")
@CrossOrigin(origins = "*") // Cho phép FE gọi (sau này đổi thành domain thật)
public class FoodAdminController {

    @Autowired
    private FoodAdminService foodAdminService;

    // 1. Lấy danh sách món
    @GetMapping
    public ResponseEntity<List<FoodEntity>> getAllFoods() {
        return ResponseEntity.ok(foodAdminService.getAllFoods());
    }

    // 2. Thêm món mới
    @PostMapping
    public ResponseEntity<?> addFood(
            @RequestParam String name,
            @RequestParam(required = false) String englishName,
            @RequestParam Double calories,
            @RequestParam Double protein,
            @RequestParam Double fat,
            @RequestParam Double saturatedFat,
            @RequestParam Double carbs,
            @RequestParam Double sugar,
            @RequestParam Double fiber,
            @RequestParam Double sodium,
            @RequestParam Double potassium,
            @RequestParam Double cholesterol,
            @RequestParam String servingSize,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) MultipartFile image) throws Exception {

        FoodEntity food = foodAdminService.addFoodWithImage(
                name, englishName, calories, protein, fat, saturatedFat,
                carbs, sugar, fiber, sodium, potassium, cholesterol,
                servingSize, note, image);

        return ResponseEntity.ok(Map.of(
                "message", "Thêm món thành công!",
                "food", food
        ));
    }

    // 3. Sửa món
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

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật món thành công!",
                "food", updated
        ));
    }

    // 4. Xóa món
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFood(@PathVariable Long id) {
        foodAdminService.deleteFood(id);
        return ResponseEntity.ok(Map.of("message", "Xóa món thành công!"));
    }
}