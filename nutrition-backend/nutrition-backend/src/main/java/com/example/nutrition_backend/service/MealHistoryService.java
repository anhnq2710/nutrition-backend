package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.MealHistoryRequest;
import com.example.nutrition_backend.dto.SaveMealResponse;
import com.example.nutrition_backend.dto.MealType;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealHistoryService {

    private final MealHistoryRepository mealRepo;
    private final HealthProfileRepository profileRepo;
    private final DiseaseLimitRepository diseaseLimitRepository;

    public MealHistoryService(MealHistoryRepository mealRepo,
                              HealthProfileRepository profileRepo,
                              DiseaseLimitRepository diseaseLimitRepository) {
        this.mealRepo = mealRepo;
        this.profileRepo = profileRepo;
        this.diseaseLimitRepository = diseaseLimitRepository;
    }

    // 1. Lưu từ DTO (giữ nguyên – dùng khi FE gửi trực tiếp)
    public SaveMealResponse saveMealWithWarning(MealHistoryRequest req) {
        if (req.getUserId() == null || req.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (req.getFoodName() == null || req.getFoodName().isBlank()) {
            throw new IllegalArgumentException("foodName is required");
        }

        MealType mealType = req.getMealType() != null ? req.getMealType() : MealType.SNACK;
        LocalDate mealDate = req.getMealDate() != null ? req.getMealDate() : LocalDate.now();

        MealHistory meal = new MealHistory();
        meal.setUserId(req.getUserId());
        meal.setFoodName(req.getFoodName());
        meal.setCalories(req.getCalories());
        meal.setProtein(req.getProtein());
        meal.setFat(req.getFat());
        meal.setCarbs(req.getCarbs());
        meal.setSugar(req.getSugar());
        meal.setSodium(req.getSodium());
        meal.setMealDate(mealDate);
        meal.setMealType(mealType.name());

        MealHistory saved = mealRepo.save(meal);

        return buildSaveMealResponse(saved, mealDate);
    }

    // 2. MỚI: Lưu từ FoodEntity (món cơ bản) – TỰ ĐỘNG TÍNH CHỈ SỐ THỰC TẾ TỪ 100G
    public SaveMealResponse saveMealFromFood(String userId, FoodEntity food, MealType mealType) {
        if (food == null) throw new IllegalArgumentException("food is required");

        MealType type = mealType != null ? mealType : MealType.SNACK;
        LocalDate mealDate = LocalDate.now();

        double multiplier = food.getServingMultiplier() != null ? food.getServingMultiplier() : 1.0;

        MealHistory meal = new MealHistory();
        meal.setUserId(userId);
        meal.setFoodName(food.getName());
        meal.setCalories(food.getCalories() * multiplier);
        meal.setProtein(food.getProtein() != null ? food.getProtein() * multiplier : null);
        meal.setFat(food.getFat() != null ? food.getFat() * multiplier : null);
        meal.setCarbs(food.getCarbs() != null ? food.getCarbs() * multiplier : null);
        meal.setSugar(food.getSugar() != null ? food.getSugar() * multiplier : null);
        meal.setSodium(food.getSodium() != null ? food.getSodium() * multiplier : null);
        meal.setMealDate(mealDate);
        meal.setMealType(type.name());

        MealHistory saved = mealRepo.save(meal);

        return buildSaveMealResponse(saved, mealDate);
    }

    // Helper chung: tính tổng ngày + cảnh báo
    private SaveMealResponse buildSaveMealResponse(MealHistory saved, LocalDate mealDate) {
        List<MealHistory> todays = mealRepo.findByUserIdAndMealDate(saved.getUserId(), mealDate);

        double totalCalories = todays.stream().mapToDouble(m -> m.getCalories() != null ? m.getCalories() : 0).sum();
        double totalSugar = todays.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).sum();
        double totalSodium = todays.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).sum();
        double totalFat = todays.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).sum();

        Map<String, Double> thresholds = new HashMap<>();
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(saved.getUserId());

        double calLimit = Double.NaN, sugarLimit = Double.NaN, sodiumLimit = Double.NaN, fatLimit = Double.NaN;

        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();
            calLimit = profile.getDailyCalorieLimit();
            thresholds.put("calorieLimit", calLimit);

            DiseaseLimit dl = profile.getDisease();
            if (dl != null) {
                if (dl.getSugarMax() != null) sugarLimit = dl.getSugarMax();
                if (dl.getSodiumMax() != null) sodiumLimit = dl.getSodiumMax();
                if (dl.getFatMax() != null) fatLimit = dl.getFatMax();
            }
        }

        Map<String, Integer> perMetricSeverity = new HashMap<>();
        int overall = 0;
        List<String> warnings = new ArrayList<>();

        int calSeverity = computeSeverity(totalCalories, calLimit);
        perMetricSeverity.put("calories", calSeverity);
        overall = Math.max(overall, calSeverity);
        if (calSeverity > 0) warnings.add("Tổng calo hôm nay vượt ngưỡng!");

        int sugarSeverity = computeSeverity(totalSugar, sugarLimit);
        perMetricSeverity.put("sugar", sugarSeverity);
        overall = Math.max(overall, sugarSeverity);
        if (sugarSeverity > 0) warnings.add("Tổng đường hôm nay cao!");

        int sodiumSeverity = computeSeverity(totalSodium, sodiumLimit);
        perMetricSeverity.put("sodium", sodiumSeverity);
        overall = Math.max(overall, sodiumSeverity);
        if (sodiumSeverity > 0) warnings.add("Tổng natri hôm nay cao!");

        int fatSeverity = computeSeverity(totalFat, fatLimit);
        perMetricSeverity.put("fat", fatSeverity);
        overall = Math.max(overall, fatSeverity);
        if (fatSeverity > 0) warnings.add("Tổng chất béo hôm nay cao!");

        SaveMealResponse resp = new SaveMealResponse();
        resp.setSavedMeal(saved);

        Map<String, Double> dailyTotals = new HashMap<>();
        dailyTotals.put("totalCalories", totalCalories);
        dailyTotals.put("totalSugar", totalSugar);
        dailyTotals.put("totalSodium", totalSodium);
        dailyTotals.put("totalFat", totalFat);
        resp.setDailyTotals(dailyTotals);

        resp.setThresholds(thresholds);
        resp.setPerMetricSeverity(perMetricSeverity);
        resp.setWarningLevel(overall);
        resp.setWarnings(warnings);

        return resp;
    }

    private int computeSeverity(double actual, double limit) {
        if (Double.isNaN(limit) || limit <= 0) return 0;
        if (actual <= limit) return 0;
        if (actual <= limit * 1.5) return 1;
        return 2;
    }

    // Lấy lịch sử (giữ nguyên)
    public List<MealHistory> getHistoryByUserAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return mealRepo.findByUserId(userId);
        }
        if (startDate != null && endDate == null) endDate = startDate;
        if (startDate == null && endDate != null) startDate = endDate;

        return mealRepo.findByUserIdAndMealDateBetween(userId, startDate, endDate);
    }

    @Transactional
    public void deleteEntireMeal(String userId, LocalDate mealDate, String mealType) {
        mealRepo.deleteByUserIdAndMealDateAndMealType(userId, mealDate, mealType);
    }

    @Transactional
    public void deleteMultipleMeals(String userId, List<Long> ids) {
        List<MealHistory> meals = mealRepo.findAllById(ids);

        // Kiểm tra quyền: chỉ xóa món của chính user
        for (MealHistory meal : meals) {
            if (!meal.getUserId().equals(userId)) {
                throw new RuntimeException("Không có quyền xóa món ID: " + meal.getId());
            }
        }

        mealRepo.deleteAllById(ids);
    }
}