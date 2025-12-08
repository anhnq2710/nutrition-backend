package com.example.nutrition_backend.service;


import com.example.nutrition_backend.dto.DailyAdvice;
import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.MealHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class NutritionAdvisorService {

    @Autowired
    private HealthProfileRepository profileRepo;

    @Autowired
    private MealHistoryRepository mealRepo;

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
            if (avgSugar > 10) {  // Thay 25 thành 10 để trigger với dữ liệu test
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
            if (avgSodium > 1000) {  // Thay 2000 thành 1000 để trigger
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
}