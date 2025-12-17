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
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FoodAdminService {

    @Autowired
    private FoodRepository foodRepo;

    private static final String UPLOAD_DIR = "src/main/resources/static/food-images/"; // ĐÚNG ĐỂ CHẠY TRONG JAR

    public FoodEntity addFoodWithImage(
            String name, String englishName,
            Double caloriesPer100g, Double proteinPer100g, Double fatPer100g, Double saturatedFatPer100g,
            Double carbsPer100g, Double sugarPer100g, Double fiberPer100g, Double sodiumPer100g,
            Double potassiumPer100g, Double cholesterolPer100g,
            String servingSize, String note,
            MultipartFile image) throws Exception {

        // Kiểm tra trùng tên
        if (foodRepo.existsByName(name.trim())) {
            throw new RuntimeException("Món ăn '" + name + "' đã tồn tại!");
        }

        // Upload ảnh
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = UUID.randomUUID() + fileExtension;
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(image.getInputStream(), path);
            imageUrl = "/food-images/" + fileName;
        }

        FoodEntity food = new FoodEntity();
        food.setName(name.trim());
        food.setEnglishName(englishName);

        // Chỉ số đã là cho 100g
        food.setCalories(caloriesPer100g);
        food.setProtein(proteinPer100g);
        food.setFat(fatPer100g);
        food.setSaturatedFat(saturatedFatPer100g);
        food.setCarbs(carbsPer100g);
        food.setSugar(sugarPer100g);
        food.setFiber(fiberPer100g);
        food.setSodium(sodiumPer100g);
        food.setPotassium(potassiumPer100g);
        food.setCholesterol(cholesterolPer100g);

        food.setServingSize(servingSize);
        food.setNote(note);
        food.setImageUrl(imageUrl);

        // Chuẩn hóa 100g
        food.setPer100g(true);

        // Tính serving_multiplier từ servingSize (ví dụ "1 tô (350g)" → 3.5)
        double multiplier = parseServingGram(servingSize) / 100.0;
        food.setServingMultiplier(multiplier > 0 ? multiplier : 1.0); // mặc định 1.0 nếu không parse được

        return foodRepo.save(food);
    }

    // Hàm phụ: Parse số gram từ servingSize như "1 tô (350g)" → 350
    private double parseServingGram(String servingSize) {
        if (servingSize == null || servingSize.isBlank()) return 100.0; // mặc định 100g

        Pattern pattern = Pattern.compile("(\\d+)\\s*g");
        Matcher matcher = pattern.matcher(servingSize);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 100.0; // mặc định nếu không tìm thấy
    }

    // Trong FoodAdminService.java
    public List<FoodEntity> getAllFoods() {
        return foodRepo.findAll();
    }

    public FoodEntity updateFoodWithImage(Long id, String name, String englishName,
                                          Double calories, Double protein, Double fat, Double saturatedFat,
                                          Double carbs, Double sugar, Double fiber, Double sodium,
                                          Double potassium, Double cholesterol,
                                          String servingSize, String note,
                                          MultipartFile image) throws Exception {

        FoodEntity existing = foodRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ID: " + id));

        // Chỉ cập nhật nếu param không null
        if (name != null) existing.setName(name);
        if (englishName != null) existing.setEnglishName(englishName);
        if (calories != null) existing.setCalories(calories);
        if (protein != null) existing.setProtein(protein);
        if (fat != null) existing.setFat(fat);
        if (saturatedFat != null) existing.setSaturatedFat(saturatedFat);
        if (carbs != null) existing.setCarbs(carbs);
        if (sugar != null) existing.setSugar(sugar);
        if (fiber != null) existing.setFiber(fiber);
        if (sodium != null) existing.setSodium(sodium);
        if (potassium != null) existing.setPotassium(potassium);
        if (cholesterol != null) existing.setCholesterol(cholesterol);
        if (servingSize != null) existing.setServingSize(servingSize);
        if (note != null) existing.setNote(note);

        // Upload ảnh mới nếu có
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename()
                    .replaceAll("[^a-zA-Z0-9.-]", "_");
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(image.getInputStream(), path);
            existing.setImageUrl("/food-images/" + fileName);
        }

        // Tính lại serving_multiplier nếu servingSize thay đổi
        if (servingSize != null) {
            double multiplier = parseServingGram(servingSize) / 100.0;
            existing.setServingMultiplier(multiplier > 0 ? multiplier : 1.0);
        }

        return foodRepo.save(existing);
    }

    public void deleteFood(Long id) {
        if (!foodRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy món ID: " + id);
        }
        foodRepo.deleteById(id);
    }
}