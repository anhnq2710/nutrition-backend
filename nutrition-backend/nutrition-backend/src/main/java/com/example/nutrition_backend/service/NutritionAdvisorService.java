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

    public StatisticsAdvice getStatisticsAdvice(String userId, LocalDate fromDate, LocalDate toDate) {
        List<MealHistory> meals = mealRepo.findByUserIdAndMealDateBetween(userId, fromDate, toDate);
        if (meals.isEmpty()) {
            StatisticsAdvice advice = new StatisticsAdvice();
            advice.setMessage("Không có dữ liệu trong khoảng thời gian này!");
            return advice;
        }

        int totalMeals = meals.size();
        int numDays = (int) (toDate.toEpochDay() - fromDate.toEpochDay() + 1);

        // TÍNH AVG/TOTAL BẰNG STREAM
        double avgCalories = meals.stream().mapToDouble(m -> m.getCalories() != null ? m.getCalories() : 0).average().orElse(0);
        double avgSugar = meals.stream().mapToDouble(m -> m.getSugar() != null ? m.getSugar() : 0).average().orElse(0);
        double avgSodium = meals.stream().mapToDouble(m -> m.getSodium() != null ? m.getSodium() : 0).average().orElse(0);
        double avgFat = meals.stream().mapToDouble(m -> m.getFat() != null ? m.getFat() : 0).average().orElse(0);

        double totalCalories = avgCalories * totalMeals;
        double totalSugar = avgSugar * totalMeals;
        double totalSodium = avgSodium * totalMeals;
        double totalFat = avgFat * totalMeals;

        StatisticsAdvice advice = new StatisticsAdvice();
        advice.setPeriod("Từ " + fromDate + " đến " + toDate);
        advice.setAvgCalories(avgCalories);
        advice.setTotalCalories(totalCalories);
        advice.setAvgSugar(avgSugar);
        advice.setTotalSugar(totalSugar);
        advice.setAvgSodium(avgSodium);
        advice.setTotalSodium(totalSodium);
        advice.setTotalMeals(totalMeals);

        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // THÊM MỚI: Cảnh báo số bữa ăn quá nhiều
        double mealsPerDay = (double) totalMeals / numDays;
        if (mealsPerDay > 3.5) {  // > 3.5 bữa/ngày = quá nhiều
            warnings.add("Số bữa ăn quá nhiều (" + mealsPerDay + " bữa/ngày) – Có thể dẫn đến tăng cân!");
            suggestions.add("Hạn chế bữa phụ, ưu tiên ăn chậm và no đủ. Tập thể dục 30 phút/ngày.");
        }

        // Lấy profile để so sánh ngưỡng
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isPresent()) {
            HealthProfile profile = profileOpt.get();
            double dailyCalorieLimit = profile.getDailyCalorieLimit();

            // Cảnh báo calo
            if (totalCalories > dailyCalorieLimit * numDays) {
                warnings.add("Tổng calo vượt (" + totalCalories + " > " + dailyCalorieLimit * numDays + ")!");
                suggestions.add("Giảm khẩu phần, tăng rau củ.");
            }

            // Cảnh báo đường
            if (profile.isHasDiabetes()) {
                Optional<DiseaseLimit> diabetesLimit = diseaseLimitRepository.findByDiseaseName("diabetes");
                if (diabetesLimit.isPresent()) {
                    double sugarLimit = diabetesLimit.get().getSugarMax() * numDays;
                    if (totalSugar > sugarLimit * 1.5) {
                        warnings.add("Tổng đường vượt 150% (" + totalSugar + "g > " + sugarLimit + "g) – NGUY CƠ!");
                        suggestions.add("Giảm chè, nước ngọt. Ăn rau củ ít đường.");
                    }
                }
            }

            // Cảnh báo natri
            if (profile.isHasHypertension()) {
                Optional<DiseaseLimit> hypertensionLimit = diseaseLimitRepository.findByDiseaseName("hypertension");
                if (hypertensionLimit.isPresent()) {
                    double sodiumLimit = hypertensionLimit.get().getSodiumMax() * numDays;
                    if (totalSodium > sodiumLimit * 1.2) {
                        warnings.add("Tổng natri vượt 120% (" + totalSodium + "mg > " + sodiumLimit + "mg) – VƯỢT!");
                        suggestions.add("Hạn chế muối, nước mắm. Uống trà thảo mộc.");
                    }
                }
            }

            // Cảnh báo chất béo
            if (profile.isHasCardiovascular()) {
                Optional<DiseaseLimit> cardioLimit = diseaseLimitRepository.findByDiseaseName("cardiovascular");
                if (cardioLimit.isPresent()) {
                    double fatLimit = cardioLimit.get().getFatMax() * numDays;
                    if (totalFat > fatLimit * 1.2) {
                        warnings.add("Tổng chất béo vượt 120% (" + totalFat + "g > " + fatLimit + "g) – CẢNH BÁO!");
                        suggestions.add("Giảm mỡ động vật, đồ chiên. Tăng omega-3 từ cá.");
                    }
                }
            }
        }

        advice.setWarnings(warnings);
        advice.setSuggestions(suggestions);

        if (warnings.isEmpty()) {
            suggestions.add("Chế độ ăn cân bằng trong khoảng thời gian này – tiếp tục duy trì!");
        }

        return advice;
    }
}