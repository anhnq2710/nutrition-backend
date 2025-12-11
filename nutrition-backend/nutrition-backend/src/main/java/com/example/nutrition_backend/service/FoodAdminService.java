// src/main/java/com/example/nutrition_backend/service/FoodAdminService.java
package com.example.nutrition_backend.service;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FoodAdminService {

    @Autowired
    private FoodRepository foodRepo;

    private static final String UPLOAD_DIR = "uploads/food-images/";

    public FoodEntity addFoodWithImage(
            String name, String englishName,
            Double calories, Double protein, Double fat, Double saturatedFat,
            Double carbs, Double sugar, Double fiber, Double sodium,
            Double potassium, Double cholesterol,
            String servingSize, String note,
            MultipartFile image) throws Exception {

        // Kiểm tra trùng tên
        if (foodRepo.existsByName(name)) {
            throw new RuntimeException("Món ăn '" + name + "' đã tồn tại!");
        }

        // Tạo thư mục upload
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename()
                    .replaceAll("[^a-zA-Z0-9.-]", "_");
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(image.getInputStream(), path);
            imageUrl = "/food-images/" + fileName;
        }

        FoodEntity food = new FoodEntity();
        food.setName(name);
        food.setEnglishName(englishName);
        food.setCalories(calories);
        food.setProtein(protein);
        food.setFat(fat);
        food.setSaturatedFat(saturatedFat);
        food.setCarbs(carbs);
        food.setSugar(sugar);
        food.setFiber(fiber);
        food.setSodium(sodium);
        food.setPotassium(potassium);
        food.setCholesterol(cholesterol);
        food.setServingSize(servingSize);
        food.setNote(note);
        food.setImageUrl(imageUrl);

        return foodRepo.save(food);
    }
}