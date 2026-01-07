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

    // Lời khuyên hàng ngày (7 ngày gần nhất)
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

        // Tiểu đường
        if (profile.isHasDiabetes()) {
            if (avgSugar > 15) { // điều chỉnh ngưỡng cho thực tế
                warnings.add("Đường trung bình 7 ngày: " + String.format("%.1f", avgSugar) + "g → Hơi cao!");
                suggestions.add("Giảm chè, nước ngọt. Ăn rau củ ít đường (bí đỏ, khổ qua).");
            }
            if (profile.getHba1c() != null && profile.getHba1c() > 8.0) {
                warnings.add("HbA1c cao (" + profile.getHba1c() + "%) → Cần gặp bác sĩ ngay!");
                suggestions.add("Theo dõi đường huyết hàng ngày. Ăn đúng giờ.");
            }
        }

        // Cao huyết áp
        if (profile.isHasHypertension()) {
            if (avgSodium > 800) {
                warnings.add("Natri trung bình: " + String.format("%.0f", avgSodium) + "mg → Cao!");
                suggestions.add("Giới hạn natri <1500mg/ngày. Không uống hết nước dùng/mắm muối. Dùng chanh, tỏi thay thế.");
            }
            if (profile.getBloodPressureSystolic() != null && profile.getBloodPressureSystolic() > 140) {
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
            if (profile.getCholesterolTotal() != null && profile.getCholesterolTotal() > 200) {
                warnings.add("Cholesterol cao (" + profile.getCholesterolTotal() + "mg/dL) → Kiểm tra định kỳ!");
                suggestions.add("Ăn yến mạch, táo để giảm cholesterol xấu.");
            }
        }

        // Calo chung
        if (avgCalories > profile.getDailyCalorieLimit() * 1.1) {
            warnings.add("Calo trung bình vượt giới hạn (" + String.format("%.0f", avgCalories) + " > " + String.format("%.0f", profile.getDailyCalorieLimit()) + ")!");
            suggestions.add("Giảm khẩu phần cơm, tăng rau củ. Uống nhiều nước.");
        } else if (avgCalories < profile.getDailyCalorieLimit() * 0.7) {
            warnings.add("Calo trung bình thấp (" + String.format("%.0f", avgCalories) + ")!");
            suggestions.add("Tăng protein từ thịt nạc, cá để duy trì năng lượng.");
        }

        if (warnings.isEmpty()) {
            suggestions.add("Tiếp tục duy trì chế độ ăn cân bằng. Uống đủ 2 lít nước/ngày.");
        }

        advice.setWarnings(warnings);
        advice.setSuggestions(suggestions);

        return advice;
    }

    // Lời khuyên cho 1 món ăn cụ thể (khi search)
    public Map<String, Object> getAdviceForFood(String userId, FoodEntity food) {
        Map<String, Object> advice = new HashMap<>();
        advice.put("foodName", food.getName());

        // Tính chỉ số thực tế cho serving
        double multiplier = food.getServingMultiplier() != null ? food.getServingMultiplier() : 1.0;
        double actualCalories = food.getCalories() * multiplier;
        double actualSugar = food.getSugar() != null ? food.getSugar() * multiplier : 0;
        double actualSodium = food.getSodium() != null ? food.getSodium() * multiplier : 0;
        double actualFat = food.getFat() != null ? food.getFat() * multiplier : 0;

        advice.put("actualCalories", Math.round(actualCalories * 10) / 10.0);

        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();

            DiseaseLimit limit = profile.getDisease();

            if (limit != null) {
                if (limit.getSugarMax() != null && actualSugar > limit.getSugarMax()) {
                    warnings.add("Đường vượt ngưỡng (" + String.format("%.1f", actualSugar) + "g > " + limit.getSugarMax() + "g) – NGUY CƠ!");
                    suggestions.add(limit.getNote() != null ? limit.getNote() : "Giảm khẩu phần hoặc chọn món ít đường hơn.");
                }

                if (limit.getSodiumMax() != null && actualSodium > limit.getSodiumMax()) {
                    warnings.add("Natri vượt ngưỡng (" + String.format("%.0f", actualSodium) + "mg > " + limit.getSodiumMax() + "mg) – VƯỢT!");
                    suggestions.add(limit.getNote() != null ? limit.getNote() : "Hạn chế món mặn, dùng gia vị tự nhiên.");
                }

                if (limit.getFatMax() != null && actualFat > limit.getFatMax()) {
                    warnings.add("Chất béo vượt ngưỡng (" + String.format("%.1f", actualFat) + "g > " + limit.getFatMax() + "g) – CẢNH BÁO!");
                    suggestions.add(limit.getNote() != null ? limit.getNote() : "Chọn món hấp/luộc thay chiên.");
                }
            } else {
                // Fallback nếu chưa link disease
                if (profile.isHasDiabetes() && actualSugar > 25) {
                    warnings.add("Đường cao (" + String.format("%.1f", actualSugar) + "g) – Không phù hợp tiểu đường!");
                }
                if (profile.isHasHypertension() && actualSodium > 600) {
                    warnings.add("Natri cao (" + String.format("%.0f", actualSodium) + "mg) – Không phù hợp huyết áp!");
                }
            }
        }

        if (warnings.isEmpty()) {
            suggestions.add("Món ăn phù hợp với tình trạng sức khỏe hiện tại!");
        }

        advice.put("warnings", warnings);
        advice.put("suggestions", suggestions);

        return advice;
    }

    // Thống kê (giữ nguyên – MealHistory đã lưu chỉ số thực tế)
    // Thống kê đầy đủ tất cả chỉ số dinh dưỡng (MealHistory đã lưu thực tế)
    public StatisticsAdvice getStatisticsAdvice(String userId, LocalDate fromDate, LocalDate toDate) {
        List<MealHistory> meals = mealRepo.findByUserIdAndMealDateBetween(userId, fromDate, toDate);
        StatisticsAdvice advice = new StatisticsAdvice();

        if (meals.isEmpty()) {
            advice.setMessage("Không có dữ liệu trong khoảng thời gian này!");
            advice.setPeriod("Từ " + fromDate + " đến " + toDate);
            advice.setFullIndices(Collections.emptyMap());
            advice.setThresholds(Collections.emptyMap());
            advice.setPerMetricSeverity(Collections.emptyMap());
            advice.setWarningLevel(0);
            advice.setWarnings(Collections.emptyList());
            advice.setSuggestions(Collections.emptyList());
            return advice;
        }

        int totalMeals = meals.size();
        LocalDate start = fromDate.isBefore(toDate) ? fromDate : toDate;
        LocalDate end = fromDate.isBefore(toDate) ? toDate : fromDate;
        long numDays = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        if (numDays <= 0) numDays = 1; // Phòng trường hợp lỗi

        // Tính tổng cho tất cả chỉ số
        double totalCalories = meals.stream().mapToDouble(m -> m.getCalories() != null ? m.getCalories() : 0).sum();
        double totalProtein = meals.stream().mapToDouble(m -> m.getProtein() != null ? m.getProtein() : 0).sum();
        double totalFat = meals.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).sum();
        double totalSaturatedFat = meals.stream().mapToDouble(m -> m.getSaturatedFat() != null ? m.getSaturatedFat() : 0).sum();
        double totalCarbs = meals.stream().mapToDouble(m -> m.getCarbs() != null ? m.getCarbs() : 0).sum();
        double totalSugar = meals.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).sum();
        double totalFiber = meals.stream().mapToDouble(m -> m.getFiber() != null ? m.getFiber() : 0).sum();
        double totalSodium = meals.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).sum();
        double totalPotassium = meals.stream().mapToDouble(m -> m.getPotassium() != null ? m.getPotassium() : 0).sum();
        double totalCholesterol = meals.stream().mapToDouble(m -> m.getCholesterol() != null ? m.getCholesterol() : 0).sum();

        // Trung bình mỗi bữa
        double avgCalories = totalCalories / totalMeals;
        double avgProtein = totalProtein / totalMeals;
        double avgFat = totalFat / totalMeals;
        double avgSaturatedFat = totalSaturatedFat / totalMeals;
        double avgCarbs = totalCarbs / totalMeals;
        double avgSugar = totalSugar / totalMeals;
        double avgFiber = totalFiber / totalMeals;
        double avgSodium = totalSodium / totalMeals;
        double avgPotassium = totalPotassium / totalMeals;
        double avgCholesterol = totalCholesterol / totalMeals;

        // Trung bình mỗi ngày
        double caloriesPerDay = totalCalories / numDays;
        double proteinPerDay = totalProtein / numDays;
        double fatPerDay = totalFat / numDays;
        double saturatedFatPerDay = totalSaturatedFat / numDays;
        double carbsPerDay = totalCarbs / numDays;
        double sugarPerDay = totalSugar / numDays;
        double fiberPerDay = totalFiber / numDays;
        double sodiumPerDay = totalSodium / numDays;
        double potassiumPerDay = totalPotassium / numDays;
        double cholesterolPerDay = totalCholesterol / numDays;

        // Gán thông tin cơ bản
        advice.setPeriod("Từ " + fromDate + " đến " + toDate);
        advice.setTotalMeals(totalMeals);

        // fullIndices – chứa tất cả chỉ số chi tiết
        Map<String, Object> fullIndices = new LinkedHashMap<>();
        fullIndices.put("totalCalories", totalCalories);
        fullIndices.put("avgCaloriesPerMeal", avgCalories);
        fullIndices.put("caloriesPerDay", caloriesPerDay);

        fullIndices.put("totalProtein", totalProtein);
        fullIndices.put("avgProteinPerMeal", avgProtein);
        fullIndices.put("proteinPerDay", proteinPerDay);

        fullIndices.put("totalFat", totalFat);
        fullIndices.put("avgFatPerMeal", avgFat);
        fullIndices.put("fatPerDay", fatPerDay);

        fullIndices.put("totalSaturatedFat", totalSaturatedFat);
        fullIndices.put("avgSaturatedFatPerMeal", avgSaturatedFat);
        fullIndices.put("saturatedFatPerDay", saturatedFatPerDay);

        fullIndices.put("totalCarbs", totalCarbs);
        fullIndices.put("avgCarbsPerMeal", avgCarbs);
        fullIndices.put("carbsPerDay", carbsPerDay);

        fullIndices.put("totalSugar", totalSugar);
        fullIndices.put("avgSugarPerMeal", avgSugar);
        fullIndices.put("sugarPerDay", sugarPerDay);

        fullIndices.put("totalFiber", totalFiber);
        fullIndices.put("avgFiberPerMeal", avgFiber);
        fullIndices.put("fiberPerDay", fiberPerDay);

        fullIndices.put("totalSodium", totalSodium);
        fullIndices.put("avgSodiumPerMeal", avgSodium);
        fullIndices.put("sodiumPerDay", sodiumPerDay);

        fullIndices.put("totalPotassium", totalPotassium);
        fullIndices.put("avgPotassiumPerMeal", avgPotassium);
        fullIndices.put("potassiumPerDay", potassiumPerDay);

        fullIndices.put("totalCholesterol", totalCholesterol);
        fullIndices.put("avgCholesterolPerMeal", avgCholesterol);
        fullIndices.put("cholesterolPerDay", cholesterolPerDay);

        advice.setFullIndices(fullIndices);

        // Phần thresholds, cảnh báo, suggestions (giữ nguyên logic cũ của bạn)
        Map<String, Object> thresholds = new LinkedHashMap<>();
        Map<String, Integer> perMetricSeverity = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        int overallWarning = 0;

        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();
            double dailyCalorieLimit = profile.getDailyCalorieLimit();
            double calLimitPeriod = dailyCalorieLimit * numDays;
            thresholds.put("calorieLimitPeriod", calLimitPeriod);
            int calSeverity = computeSeverity(totalCalories, calLimitPeriod);
            perMetricSeverity.put("calories", calSeverity);
            overallWarning = Math.max(overallWarning, calSeverity);
            if (calSeverity > 0) {
                warnings.add("Calo vượt ngưỡng trong kỳ");
                suggestions.add("Giảm khẩu phần hoặc tăng hoạt động thể chất.");
            }

            DiseaseLimit dl = profile.getDisease();
            if (dl != null) {
                // Tiểu đường (diabetes)
                if (dl.getSugarMax() != null) {
                    double sugarLimitPeriod = dl.getSugarMax() * numDays;
                    thresholds.put("sugarLimitPeriod", sugarLimitPeriod);
                    int sugarSeverity = computeSeverity(totalSugar, sugarLimitPeriod);
                    perMetricSeverity.put("sugar", sugarSeverity);
                    overallWarning = Math.max(overallWarning, sugarSeverity);
                    if (sugarSeverity > 0) {
                        warnings.add("Tổng đường vượt ngưỡng trong kỳ (tiểu đường)");
                        suggestions.add(dl.getNote() != null ? dl.getNote() : "Hạn chế đồ ngọt, tăng rau củ ít đường.");
                    }
                }

                // Tăng huyết áp (hypertension)
                if (dl.getSodiumMax() != null) {
                    double sodiumLimitPeriod = dl.getSodiumMax() * numDays;
                    thresholds.put("sodiumLimitPeriod", sodiumLimitPeriod);
                    int sodiumSeverity = computeSeverity(totalSodium, sodiumLimitPeriod);
                    perMetricSeverity.put("sodium", sodiumSeverity);
                    overallWarning = Math.max(overallWarning, sodiumSeverity);
                    if (sodiumSeverity > 0) {
                        warnings.add("Tổng natri/muối vượt ngưỡng trong kỳ (tăng huyết áp)");
                        suggestions.add("Giảm mặn, tránh đồ hộp, thức ăn nhanh.");
                    }
                }

                // Bệnh tim mạch (cardiovascular)
                if (dl.getFatMax() != null) {
                    double fatLimitPeriod = dl.getFatMax() * numDays;
                    thresholds.put("fatLimitPeriod", fatLimitPeriod);
                    int fatSeverity = computeSeverity(totalFat, fatLimitPeriod);
                    perMetricSeverity.put("fat", fatSeverity);
                    overallWarning = Math.max(overallWarning, fatSeverity);
                    if (fatSeverity > 0) {
                        warnings.add("Tổng chất béo vượt ngưỡng trong kỳ (bệnh tim mạch)");
                        suggestions.add("Giảm đồ chiên xào, ưu tiên chất béo lành mạnh từ cá, hạt.");
                    }
                }

                // Cảnh báo ngược (thiếu chất tốt)
                if (totalFiber < 25 * numDays) { // Khuyến nghị trung bình 25-30g/ngày
                    warnings.add("Thiếu chất xơ trong kỳ");
                    suggestions.add("Tăng rau xanh, trái cây, ngũ cốc nguyên cám.");
                }
                if (totalPotassium < 3000 * numDays) { // Khuyến nghị ~3500mg/ngày
                    warnings.add("Thiếu kali trong kỳ");
                    suggestions.add("Ăn chuối, khoai lang, rau bina để cân bằng natri.");
                }
            }
        }

        if (warnings.isEmpty()) {
            suggestions.add("Chế độ ăn cân bằng – tiếp tục duy trì!");
        }

        advice.setThresholds(thresholds);
        advice.setPerMetricSeverity(perMetricSeverity);
        advice.setWarnings(warnings);
        advice.setSuggestions(suggestions);
        advice.setWarningLevel(overallWarning);
        advice.setMessage(warnings.isEmpty() ? "Không có cảnh báo lớn." : "Đã phát hiện một số cảnh báo.");

        return advice;
    }

    private int computeSeverity(double actual, double limit) {
        if (limit <= 0) return 0;
        if (actual <= limit) return 0;
        if (actual <= limit * 1.5) return 1;
        return 2;
    }
}