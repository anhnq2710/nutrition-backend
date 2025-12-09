
package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.repository.FoodRepository;
import com.example.nutrition_backend.service.FoodAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/food")
public class FoodAdminController {

    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private FoodAdminService foodAdminService;

    @PostMapping("/add")
    public ResponseEntity<?> addFood(
            @RequestParam("name") String name,
            @RequestParam("englishName") String englishName,
            @RequestParam("calories") Double calories,
            @RequestParam("protein") Double protein,
            @RequestParam("fat") Double fat,
            @RequestParam(value = "saturatedFat", required = false, defaultValue = "0") Double saturatedFat,
            @RequestParam("carbs") Double carbs,
            @RequestParam("sugar") Double sugar,
            @RequestParam(value = "fiber", required = false, defaultValue = "0") Double fiber,
            @RequestParam("sodium") Double sodium,
            @RequestParam(value = "potassium", required = false, defaultValue = "0") Double potassium,
            @RequestParam(value = "cholesterol", required = false, defaultValue = "0") Double cholesterol,
            @RequestParam(value = "servingSize", required = false) String servingSize,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            FoodEntity saved = foodAdminService.addFoodWithImage(
                    name, englishName, calories, protein, fat, saturatedFat,
                    carbs, sugar, fiber, sodium, potassium, cholesterol,
                    servingSize, note, image);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}