package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.MealHistoryRequest;
import com.example.nutrition_backend.dto.SaveMealResponse;
import com.example.nutrition_backend.dto.MealType;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
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

    // existing save if needed kept elsewhere...

    // New: save from DTO and return response with warnings
    public SaveMealResponse saveMealWithWarning(MealHistoryRequest req) {
        // validate basic fields
        if (req.getUserId() == null || req.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (req.getFoodName() == null || req.getFoodName().isBlank()) {
            throw new IllegalArgumentException("foodName is required");
        }
        if (req.getMealType() == null) {
            // default nếu không truyền: SNACK
            req.setMealType(MealType.SNACK);
        }
        // mặc định mealDate = today nếu null
        LocalDate mealDate = req.getMealDate() == null ? LocalDate.now() : req.getMealDate();

        // Build entity
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
        meal.setMealType(String.valueOf(req.getMealType()));
        // createdAt auto

        // Save meal first
        MealHistory saved = mealRepo.save(meal);

        // Tính tổng cho ngày (bao gồm bữa vừa lưu)
        List<MealHistory> todays = mealRepo.findByUserIdAndMealDate(saved.getUserId(), mealDate);

        double totalCalories = todays.stream().mapToDouble(m -> m.getCalories() != null ? m.getCalories() : 0).sum();
        double totalSugar = todays.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).sum();
        double totalSodium = todays.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).sum();
        double totalFat = todays.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).sum();

        // Lấy profile để biết ngưỡng
        Map<String, Double> thresholds = new HashMap<>();
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(saved.getUserId());

        // default thresholds = NaN => không có ngưỡng
        double calLimit = Double.NaN, sugarLimit = Double.NaN, sodiumLimit = Double.NaN, fatLimit = Double.NaN;

        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();
            // calories: dùng dailyCalorieLimit
            calLimit = profile.getDailyCalorieLimit();
            thresholds.put("calorieLimit", calLimit);

            // nếu profile link disease -> lấy diseaseLimits
            if (profile.getDisease() != null) {
                DiseaseLimit dl = profile.getDisease();
                // assume dl.* are per-day (so compare directly)
                if (dl.getSugarMax() != null) { sugarLimit = dl.getSugarMax(); thresholds.put("sugarLimit", sugarLimit); }
                if (dl.getSodiumMax() != null) { sodiumLimit = dl.getSodiumMax(); thresholds.put("sodiumLimit", sodiumLimit); }
                if (dl.getFatMax() != null) { fatLimit = dl.getFatMax(); thresholds.put("fatLimit", fatLimit); }
            } else {
                // fallback: if flags exist, try to find disease limits by flags
                if (profile.isHasDiabetes()) {
                    diseaseLimitRepository.findByDiseaseNameIgnoreCase("diabetes").ifPresent(dl -> {
                        thresholds.put("sugarLimit", dl.getSugarMax());
                    });
                }
                if (profile.isHasHypertension()) {
                    diseaseLimitRepository.findByDiseaseNameIgnoreCase("hypertension").ifPresent(dl -> {
                        thresholds.put("sodiumLimit", dl.getSodiumMax());
                    });
                }
                if (profile.isHasCardiovascular()) {
                    diseaseLimitRepository.findByDiseaseNameIgnoreCase("cardiovascular").ifPresent(dl -> {
                        thresholds.put("fatLimit", dl.getFatMax());
                    });
                }
                // set numeric local variables if present in map
                if (thresholds.containsKey("sugarLimit")) sugarLimit = (Double) thresholds.get("sugarLimit");
                if (thresholds.containsKey("sodiumLimit")) sodiumLimit = (Double) thresholds.get("sodiumLimit");
                if (thresholds.containsKey("fatLimit")) fatLimit = (Double) thresholds.get("fatLimit");
            }
        }

        // Tính severity (so sánh tổng ngày với threshold * 1 day)
        Map<String, Integer> perMetricSeverity = new HashMap<>();
        int overall = 0;
        List<String> warnings = new ArrayList<>();

        // calories severity (với calLimit)
        int calSeverity = computeSeverity(totalCalories, calLimit);
        perMetricSeverity.put("calories", calSeverity);
        overall = Math.max(overall, calSeverity);
        if (calSeverity == 1) warnings.add("Tổng calo hôm nay hơi cao: " + Math.round(totalCalories) + " kcal (ngưỡng " + Math.round(calLimit) + " kcal).");
        if (calSeverity == 2) warnings.add("Tổng calo hôm nay rất cao: " + Math.round(totalCalories) + " kcal! Hãy chú ý khẩu phần.");

        // sugar
        int sugarSeverity = computeSeverity(totalSugar, sugarLimit);
        perMetricSeverity.put("sugar", sugarSeverity);
        overall = Math.max(overall, sugarSeverity);
        if (sugarSeverity == 1) warnings.add("Tổng đường hôm nay hơi cao: " + Math.round(totalSugar) + " g.");
        if (sugarSeverity == 2) warnings.add("Tổng đường hôm nay rất cao: " + Math.round(totalSugar) + " g! Tránh đồ ngọt.");

        // sodium
        int sodiumSeverity = computeSeverity(totalSodium, sodiumLimit);
        perMetricSeverity.put("sodium", sodiumSeverity);
        overall = Math.max(overall, sodiumSeverity);
        if (sodiumSeverity == 1) warnings.add("Tổng natri hôm nay hơi cao: " + Math.round(totalSodium) + " mg.");
        if (sodiumSeverity == 2) warnings.add("Tổng natri hôm nay rất cao: " + Math.round(totalSodium) + " mg! Hãy giảm muối.");

        // fat
        int fatSeverity = computeSeverity(totalFat, fatLimit);
        perMetricSeverity.put("fat", fatSeverity);
        overall = Math.max(overall, fatSeverity);
        if (fatSeverity == 1) warnings.add("Tổng chất béo hôm nay hơi cao: " + Math.round(totalFat) + " g.");
        if (fatSeverity == 2) warnings.add("Tổng chất béo hôm nay rất cao: " + Math.round(totalFat) + " g! Giảm mỡ động vật.");

        // Build response
        SaveMealResponse resp = new SaveMealResponse();
        resp.setSavedMeal(saved);
        Map<String, Double> dailyTotals = new HashMap<>();
        dailyTotals.put("totalCalories", totalCalories);
        dailyTotals.put("totalSugar", totalSugar);
        dailyTotals.put("totalSodium", totalSodium);
        dailyTotals.put("totalFat", totalFat);
        resp.setDailyTotals(dailyTotals);

        // fill thresholds map (ensuring keys present)
        Map<String, Double> thr = new HashMap<>();
        thr.put("calorieLimit", Double.isNaN(calLimit) ? null : calLimit);
        thr.put("sugarLimit", Double.isNaN(sugarLimit) ? null : sugarLimit);
        thr.put("sodiumLimit", Double.isNaN(sodiumLimit) ? null : sodiumLimit);
        thr.put("fatLimit", Double.isNaN(fatLimit) ? null : fatLimit);
        resp.setThresholds(thr);

        resp.setPerMetricSeverity(perMetricSeverity);
        resp.setWarningLevel(overall);
        resp.setWarnings(warnings);

        return resp;
    }

    // reuse helper (same as earlier)
    private int computeSeverity(double actual, double limit) {
        if (Double.isNaN(limit) || limit <= 0) return 0;
        if (actual <= limit) return 0;
        if (actual <= limit * 1.5) return 1;
        return 2;
    }

    public List<MealHistory> getHistoryByUserAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {

        // Nếu không truyền ngày → trả toàn bộ lịch sử
        if (startDate == null && endDate == null) {
            return mealRepo.findByUserId(userId);
        }

        // Nếu chỉ truyền startDate → endDate = startDate
        if (startDate != null && endDate == null) {
            endDate = startDate;
        }

        // Nếu chỉ truyền endDate → startDate = endDate
        if (startDate == null && endDate != null) {
            startDate = endDate;
        }

        return mealRepo.findByUserIdAndMealDateBetween(userId, startDate, endDate);
    }

}
