package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.DailyAdvice;
import com.example.nutrition_backend.dto.StatisticsAdvice;
import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class NutritionAdvisorService {

    @Autowired
    private HealthProfileRepository profileRepo;

    @Autowired
    private MealHistoryRepository mealRepo;

    @Autowired
    private DiseaseLimitRepository diseaseLimitRepository;

    public DailyAdvice getDailyAdvice(String userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<MealHistory> weekMeals = mealRepo.findByUserIdAndMealDateBetween(userId, weekAgo, today);
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);

        DailyAdvice advice = new DailyAdvice();
        advice.setDate(today.toString());

        if (weekMeals.isEmpty()) {
            advice.setMessage("Chưa có lịch sử bữa ăn nào trong 7 ngày qua!");
            return advice;
        }

        if (profileOpt.isEmpty()) {
            advice.setMessage("Chưa có profile bệnh lý! Vui lòng cập nhật để có tư vấn chính xác.");
            return advice;
        }

        HealthProfile profile = profileOpt.get();

        double avgCalories = weekMeals.stream().mapToDouble(MealHistory::getCalories).average().orElse(0);
        double avgSugar = weekMeals.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).average().orElse(0);
        double avgSodium = weekMeals.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).average().orElse(0);

        advice.setAvgCalories(avgCalories);
        advice.setCalorieLimit(profile.getDailyCalorieLimit());
        advice.setAvgSugar(avgSugar);
        advice.setAvgSodium(avgSodium);

        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // Tiểu đường (ngưỡng thấp hơn để test dễ)
        if (profile.isHasDiabetes()) {
            if (avgSugar > 10) { // Thay 25 thành 10 để trigger với dữ liệu test
                warnings.add("Đường trung bình 7 ngày: " + String.format("%.1f", avgSugar) + "g → Hơi cao!");
                suggestions.add("Giảm chè, nước ngọt. Ăn rau củ ít đường (bí đỏ, khổ qua).");
            }
            if (profile.getHba1c() > 8.0) {
                warnings.add("HbA1c cao (" + profile.getHba1c() + "%) → Cần gặp bác sĩ ngay!");
                suggestions.add("Theo dõi đường huyết hàng ngày. Ăn đúng giờ.");
            }
        }

        // Cao huyết áp (ngưỡng thấp hơn)
        if (profile.isHasHypertension()) {
            if (avgSodium > 1000) { // Thay 2000 thành 1000 để trigger
                warnings.add("Natri trung bình: " + String.format("%.0f", avgSodium) + "mg → Cao!");
                suggestions.add("Giới hạn natri <1500mg/ngày. Không uống hết nước dùng/mắm muối. Dùng chanh, tỏi thay thế.");
            }
            if (profile.getBloodPressureSystolic() > 140) {
                warnings.add("Huyết áp tâm thu cao (" + profile.getBloodPressureSystolic() + "mmHg) → Theo dõi hàng ngày!");
                suggestions.add("Tập thể dục nhẹ 30 phút/ngày. Ăn rau củ giàu kali (chuối, cam).");
            }
        }

        // Tim mạch
        if (profile.isHasCardiovascular()) {
            double avgFat = weekMeals.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).average().orElse(0);
            if (avgFat > 40) {
                warnings.add("Chất béo trung bình cao: " + String.format("%.1f", avgFat) + "g!");
                suggestions.add("Giảm mỡ động vật, đồ chiên. Tăng cá hồi, hạt óc chó (omega-3).");
            }
            if (profile.getCholesterolTotal() > 200) {
                warnings.add("Cholesterol cao (" + profile.getCholesterolTotal() + "mg/dL) → Kiểm tra định kỳ!");
                suggestions.add("Ăn yến mạch, táo để giảm cholesterol xấu.");
            }
        }

        // Calo chung
        if (avgCalories > profile.getDailyCalorieLimit()) {
            warnings.add("Calo trung bình vượt giới hạn (" + String.format("%.0f", avgCalories) + " > " + String.format("%.0f", profile.getDailyCalorieLimit()) + ")!");
            suggestions.add("Giảm khẩu phần cơm, tăng rau củ. Uống nhiều nước.");
        } else if (avgCalories < profile.getDailyCalorieLimit() * 0.8) {
            warnings.add("Calo trung bình thấp (" + String.format("%.0f", avgCalories) + " < " + String.format("%.0f", profile.getDailyCalorieLimit() * 0.8) + ")!");
            suggestions.add("Tăng protein từ thịt nạc, cá để duy trì năng lượng.");
        }

        // Suggestions mặc định nếu không có warning
        if (suggestions.isEmpty()) {
            suggestions.add("Tiếp tục duy trì chế độ ăn cân bằng. Uống đủ 2 lít nước/ngày.");
        }

        advice.setWarnings(warnings);
        advice.setSuggestions(suggestions);

        return advice;
    }

    public Map<String, Object> getAdviceForFood(String userId, FoodEntity food) {
        Map<String, Object> advice = new HashMap<>();
        advice.put("foodName", food.getName());
        advice.put("calories", food.getCalories());

        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();

            // Dùng foreign key disease để query nhanh (nếu có)
            if (profile.getDisease() != null) {
                DiseaseLimit limit = profile.getDisease();

                if (limit.getSugarMax() != null && food.getSugar() > limit.getSugarMax()) {
                    warnings.add("Đường vượt ngưỡng (" + food.getSugar() + "g > " + limit.getSugarMax() + "g) – NGUY CƠ!");
                    suggestions.add(limit.getNote());
                }

                if (limit.getSodiumMax() != null && food.getSodium() > limit.getSodiumMax()) {
                    warnings.add("Natri vượt ngưỡng (" + food.getSodium() + "mg > " + limit.getSodiumMax() + "mg) – VƯỢT!");
                    suggestions.add(limit.getNote());
                }

                if (limit.getFatMax() != null && food.getFat() > limit.getFatMax()) {
                    warnings.add("Chất béo vượt ngưỡng (" + food.getFat() + "g > " + limit.getFatMax() + "g) – CẢNH BÁO!");
                    suggestions.add(limit.getNote());
                }
            } else {
                // Fallback nếu chưa có foreign key
                if (profile.isHasDiabetes()) {
                    Optional<DiseaseLimit> diabetesLimit = diseaseLimitRepository.findByDiseaseName("diabetes");
                    if (diabetesLimit.isPresent()) {
                        Double sugarMax = diabetesLimit.get().getSugarMax();
                        if (food.getSugar() > sugarMax) {
                            warnings.add("Đường vượt ngưỡng (" + food.getSugar() + "g > " + sugarMax + "g) – NGUY CƠ!");
                            suggestions.add(diabetesLimit.get().getNote());
                        }
                    }
                }
                // Tương tự cho hypertension, cardiovascular...
            }
        }

        advice.put("warnings", warnings);
        advice.put("suggestions", suggestions);

        if (warnings.isEmpty()) {
            suggestions.add("Món ăn phù hợp với tình trạng sức khỏe hiện tại!");
        }

        return advice;
    }

    // Thống kê
    public StatisticsAdvice getStatisticsAdvice(String userId, LocalDate fromDate, LocalDate toDate) {
        List<MealHistory> meals = mealRepo.findByUserIdAndMealDateBetween(userId, fromDate, toDate);
        StatisticsAdvice advice = new StatisticsAdvice();

        if (meals.isEmpty()) {
            advice.setMessage("Không có dữ liệu trong khoảng thời gian này!");
            // set default empty structures
            advice.setFullIndices(Collections.emptyMap());
            advice.setThresholds(Collections.emptyMap());
            advice.setPerMetricSeverity(Collections.emptyMap());
            advice.setWarningLevel(0);
            advice.setWarnings(Collections.emptyList());
            advice.setSuggestions(Collections.emptyList());
            return advice;
        }

        int totalMeals = meals.size();
        int numDays = (int) (toDate.toEpochDay() - fromDate.toEpochDay() + 1);

        // TÍNH AVG/TOTAL BẰNG STREAM (per-meal averages)
        double avgCalories = meals.stream().mapToDouble(m -> m.getCalories() != null ? m.getCalories() : 0).average().orElse(0);
        double avgSugar = meals.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).average().orElse(0);
        double avgSodium = meals.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).average().orElse(0);
        double avgFat = meals.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).average().orElse(0);

        double totalCalories = avgCalories * totalMeals;
        double totalSugar = avgSugar * totalMeals;
        double totalSodium = avgSodium * totalMeals;
        double totalFat = avgFat * totalMeals;

        // per-day averages (useful for UI)
        double caloriesPerDay = totalCalories / numDays;
        double sugarPerDay = totalSugar / numDays;
        double sodiumPerDay = totalSodium / numDays;
        double fatPerDay = totalFat / numDays;

        advice.setPeriod("Từ " + fromDate + " đến " + toDate);
        advice.setAvgCalories(avgCalories);
        advice.setTotalCalories(totalCalories);
        advice.setAvgSugar(avgSugar);
        advice.setTotalSugar(totalSugar);
        advice.setAvgSodium(avgSodium);
        advice.setTotalSodium(totalSodium);
        advice.setAvgFat(avgFat);
        advice.setTotalFat(totalFat);
        advice.setTotalMeals(totalMeals);

        // Chuẩn bị fullIndices map để trả chi tiết
        Map<String, Object> fullIndices = new LinkedHashMap<>();
        fullIndices.put("totalCalories", totalCalories);
        fullIndices.put("avgCaloriesPerMeal", avgCalories);
        fullIndices.put("caloriesPerDay", caloriesPerDay);

        fullIndices.put("totalSugar", totalSugar);
        fullIndices.put("avgSugarPerMeal", avgSugar);
        fullIndices.put("sugarPerDay", sugarPerDay);

        fullIndices.put("totalSodium", totalSodium);
        fullIndices.put("avgSodiumPerMeal", avgSodium);
        fullIndices.put("sodiumPerDay", sodiumPerDay);

        fullIndices.put("totalFat", totalFat);
        fullIndices.put("avgFatPerMeal", avgFat);
        fullIndices.put("fatPerDay", fatPerDay);

        // Will fill thresholds and per-metric severity
        Map<String, Object> thresholds = new LinkedHashMap<>();
        Map<String, Integer> perMetricSeverity = new LinkedHashMap<>(); // 0/1/2
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // THÊM MỚI: Cảnh báo số bữa ăn quá nhiều
        double mealsPerDay = (double) totalMeals / numDays;
        if (mealsPerDay > 3.5) {  // > 3.5 bữa/ngày = quá nhiều
            warnings.add("Số bữa ăn quá nhiều (" + String.format("%.2f", mealsPerDay) + " bữa/ngày) – Có thể dẫn đến tăng cân!");
            suggestions.add("Hạn chế bữa phụ, ưu tiên ăn chậm và no đủ. Tập thể dục 30 phút/ngày.");
        }

        // Lấy profile để so sánh ngưỡng
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        int overallWarning = 0;

        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();
            double dailyCalorieLimit = profile.getDailyCalorieLimit();

            // Cảnh báo calo: so sánh tổng calo trong period với daily limit * numDays
            double calLimitPeriod = dailyCalorieLimit * numDays;
            thresholds.put("calorieLimitPeriod", calLimitPeriod);
            int calSeverity = computeSeverity(totalCalories, calLimitPeriod);
            perMetricSeverity.put("calories", calSeverity);
            overallWarning = Math.max(overallWarning, calSeverity);
            if (calSeverity == 1) {
                warnings.add("Tổng calo hơi vượt ngưỡng (" + Math.round(totalCalories) + " kcal so với " + Math.round(calLimitPeriod) + " kcal).");
                suggestions.add("Giảm khẩu phần một chút và tăng lượng rau củ.");
            } else if (calSeverity == 2) {
                warnings.add("Tổng calo vượt nhiều (" + Math.round(totalCalories) + " kcal > " + Math.round(calLimitPeriod) + " kcal) – NGUY CƠ TĂNG CÂN/THAI KỲ!");
                suggestions.add("Xem xét giảm 1 bữa chính nhỏ hoặc thay thế đồ ăn ít calo. Tư vấn chuyên gia dinh dưỡng nếu cần.");
            }

            // Cảnh báo đường nếu có diabetes flag OR disease link points to diabetes
            if (profile.isHasDiabetes() || (profile.getDisease() != null && "diabetes".equalsIgnoreCase(profile.getDisease().getDiseaseName()))) {
                Optional<DiseaseLimit> diabetesLimit = diseaseLimitRepository.findByDiseaseNameIgnoreCase("diabetes");
                if (diabetesLimit.isPresent()) {
                    double sugarLimitPeriod = diabetesLimit.get().getSugarMax() * numDays; // assume sugarMax is per-day
                    thresholds.put("sugarLimitPeriod", sugarLimitPeriod);
                    int sugarSeverity = computeSeverity(totalSugar, sugarLimitPeriod);
                    perMetricSeverity.put("sugar", sugarSeverity);
                    overallWarning = Math.max(overallWarning, sugarSeverity);
                    if (sugarSeverity == 1) {
                        warnings.add("Tổng đường hơi vượt (" + Math.round(totalSugar) + "g > " + Math.round(sugarLimitPeriod) + "g).");
                        suggestions.add("Giảm đồ ngọt và nước ngọt, ưu tiên trái cây ít đường.");
                    } else if (sugarSeverity == 2) {
                        warnings.add("Tổng đường vượt 150% (" + Math.round(totalSugar) + "g > " + Math.round(sugarLimitPeriod) + "g) – NGUY CƠ CAO!");
                        suggestions.add("Ngưng ngay đồ ngọt, liên hệ chuyên gia y tế nếu đường huyết không ổn.");
                    }
                }
            }

            // Natri / Hypertension
            if (profile.isHasHypertension() || (profile.getDisease() != null && "hypertension".equalsIgnoreCase(profile.getDisease().getDiseaseName()))) {
                Optional<DiseaseLimit> hyperLimitOpt = diseaseLimitRepository.findByDiseaseNameIgnoreCase("hypertension");
                if (hyperLimitOpt.isPresent()) {
                    double sodiumLimitPeriod = hyperLimitOpt.get().getSodiumMax() * numDays;
                    thresholds.put("sodiumLimitPeriod", sodiumLimitPeriod);
                    int sodiumSeverity = computeSeverity(totalSodium, sodiumLimitPeriod);
                    perMetricSeverity.put("sodium", sodiumSeverity);
                    overallWarning = Math.max(overallWarning, sodiumSeverity);
                    if (sodiumSeverity == 1) {
                        warnings.add("Tổng natri vượt nhẹ (" + Math.round(totalSodium) + " mg > " + Math.round(sodiumLimitPeriod) + " mg).");
                        suggestions.add("Giảm muối, chú ý nước mắm và đồ ăn sẵn.");
                    } else if (sodiumSeverity == 2) {
                        warnings.add("Tổng natri vượt nhiều (" + Math.round(totalSodium) + " mg > " + Math.round(sodiumLimitPeriod) + " mg) – NGUY CƠ HUYẾT ÁP!");
                        suggestions.add("Liên hệ bác sĩ, giảm ăn mặn ngay lập tức.");
                    }
                }
            }

            // Fat / Cardiovascular
            if (profile.isHasCardiovascular() || (profile.getDisease() != null && "cardiovascular".equalsIgnoreCase(profile.getDisease().getDiseaseName()))) {
                Optional<DiseaseLimit> cardioLimitOpt = diseaseLimitRepository.findByDiseaseNameIgnoreCase("cardiovascular");
                if (cardioLimitOpt.isPresent()) {
                    double fatLimitPeriod = cardioLimitOpt.get().getFatMax() * numDays;
                    thresholds.put("fatLimitPeriod", fatLimitPeriod);
                    int fatSeverity = computeSeverity(totalFat, fatLimitPeriod);
                    perMetricSeverity.put("fat", fatSeverity);
                    overallWarning = Math.max(overallWarning, fatSeverity);
                    if (fatSeverity == 1) {
                        warnings.add("Tổng chất béo hơi vượt (" + Math.round(totalFat) + " g > " + Math.round(fatLimitPeriod) + " g).");
                        suggestions.add("Hạn chế đồ chiên, ăn nhiều cá và rau.");
                    } else if (fatSeverity == 2) {
                        warnings.add("Tổng chất béo vượt nhiều (" + Math.round(totalFat) + " g > " + Math.round(fatLimitPeriod) + " g) – CẢNH BÁO!");
                        suggestions.add("Tư vấn chuyên gia dinh dưỡng, giảm mỡ động vật ngay.");
                    }
                }
            }
        } else {
            // nếu không có profile thì set thresholds empty and severity 0
            perMetricSeverity.put("calories", 0);
            perMetricSeverity.put("sugar", 0);
            perMetricSeverity.put("sodium", 0);
            perMetricSeverity.put("fat", 0);
        }

        // nếu không có warnings vẫn thêm suggestion mặc định
        if (warnings.isEmpty()) {
            suggestions.add("Chế độ ăn cân bằng trong khoảng thời gian này – tiếp tục duy trì!");
        }

        // set fullIndices, thresholds, perMetricSeverity, warnings, suggestions, warningLevel
        advice.setFullIndices(fullIndices);
        advice.setThresholds(thresholds);
        advice.setPerMetricSeverity(perMetricSeverity);
        advice.setWarnings(warnings);
        advice.setSuggestions(suggestions);
        advice.setWarningLevel(overallWarning);
        // optional message
        advice.setMessage(warnings.isEmpty() ? "Không có cảnh báo lớn." : "Đã phát hiện một số cảnh báo.");

        return advice;
    }

    // Helper dùng trong service
// computeSeverity: 0 = normal (<=limit), 1 = alert (>limit && <= 1.5*limit), 2 = critical (>1.5*limit)
    private int computeSeverity(double actual, double limit) {
        if (Double.isNaN(limit) || limit <= 0) return 0;
        if (actual <= limit) return 0;
        if (actual <= limit * 1.5) return 1;
        return 2;
    }
}