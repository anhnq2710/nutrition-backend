// src/main/java/com/example/nutrition_backend/service/NutritionCalculatorService.java
package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.WeightGoal;
import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionCalculatorService {

    private final DiseaseLimitRepository diseaseLimitRepo;

    public Map<String, Object> calculateDailyNeeds(
            int age,
            double heightCm,
            double weightKg,
            String gender,
            String diseaseLabel, // null nếu không có bệnh
            WeightGoal weightGoal // null → mặc định MAINTAIN
    ) {
        if (weightGoal == null) {
            weightGoal = WeightGoal.MAINTAIN;
        }

        // Tính BMR (Harris-Benedict)
        double bmr;
        if ("MALE".equalsIgnoreCase(gender)) {
            bmr = 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
        } else if ("FEMALE".equalsIgnoreCase(gender)) {
            bmr = 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        } else {
            throw new IllegalArgumentException("Giới tính phải là 'MALE' hoặc 'FEMALE'");
        }

        // TDEE với activity level trung bình (lightly active)
        double activityFactor = 1.55;
        double dailyCalorieNeeds = bmr * activityFactor;

        // Điều chỉnh theo mục tiêu cân nặng
        switch (weightGoal) {
            case LOSE -> dailyCalorieNeeds -= 500;
            case GAIN -> dailyCalorieNeeds += 500;
            default -> { /* MAINTAIN */ }
        }

        // Macro khuyến nghị hàng ngày (tương tự foodMap)
        double dailyProtein = Math.round((dailyCalorieNeeds * 0.20) / 4 * 10) / 10.0; // 20% calo từ protein
        double proteinMin = Math.round(1.6 * weightKg * 10) / 10.0;
        double proteinMax = Math.round(2.2 * weightKg * 10) / 10.0;

        double dailyCarbs = Math.round((dailyCalorieNeeds * 0.55) / 4 * 10) / 10.0; // 55% từ carbs
        double dailyFat = Math.round((dailyCalorieNeeds * 0.25) / 9 * 10) / 10.0; // 25% từ fat
        double dailySaturatedFat = Math.round(dailyFat * 0.1 * 10) / 10.0; // <10% tổng fat

        double dailySugar = Math.min(50.0, dailyCalorieNeeds * 0.10 / 4); // <10% calo từ đường thêm
        double dailyFiber = "MALE".equalsIgnoreCase(gender) ? 38.0 : 25.0; // USDA
        double dailySodium = 2300.0; // AHA
        double dailyPotassium = "MALE".equalsIgnoreCase(gender) ? 3400.0 : 2600.0;
        double dailyCholesterol = 300.0;

        // Điều chỉnh theo bệnh lý
        Map<String, Double> diseaseAdjustments = new HashMap<>();
        if (diseaseLabel != null && !diseaseLabel.isBlank()) {
            Optional<DiseaseLimit> dlOpt = diseaseLimitRepo.findByDiseaseName(diseaseLabel.toLowerCase());
            if (dlOpt.isPresent()) {
                DiseaseLimit dl = dlOpt.get();

                if (dl.getSugarMax() != null) {
                    dailySugar = Math.min(dailySugar, dl.getSugarMax() * 3); // x3 cho 3 bữa
                    diseaseAdjustments.put("dailySugarLimit", dailySugar);
                }
                if (dl.getSodiumMax() != null) {
                    dailySodium = Math.min(dailySodium, dl.getSodiumMax() * 3);
                    diseaseAdjustments.put("dailySodiumLimit", dailySodium);
                }
                if (dl.getFatMax() != null) {
                    dailyFat = Math.min(dailyFat, dl.getFatMax() * 3);
                    dailySaturatedFat = Math.min(dailySaturatedFat, dailyFat * 0.1);
                    diseaseAdjustments.put("dailyFatLimit", dailyFat);
                }
                if (dl.getCalorieMax() != null) {
                    dailyCalorieNeeds = Math.min(dailyCalorieNeeds, dl.getCalorieMax());
                }
            }
        }

        // Trả về đầy đủ tất cả chỉ số
        Map<String, Object> result = new HashMap<>();
        result.put("bmr", Math.round(bmr * 10) / 10.0);
        result.put("dailyCalorieNeeds", Math.round(dailyCalorieNeeds * 10) / 10.0);

        result.put("dailyProtein", dailyProtein);
        result.put("proteinMin", proteinMin);
        result.put("proteinMax", proteinMax);

        result.put("dailyCarbs", dailyCarbs);
        result.put("dailyFat", dailyFat);
        result.put("dailySaturatedFat", dailySaturatedFat);

        result.put("dailySugar", dailySugar);
        result.put("dailyFiber", dailyFiber);
        result.put("dailySodium", dailySodium);
        result.put("dailyPotassium", dailyPotassium);
        result.put("dailyCholesterol", dailyCholesterol);

        result.put("diseaseAdjustments", diseaseAdjustments);
        result.put("weightGoal", weightGoal.name());

        return result;
    }
}