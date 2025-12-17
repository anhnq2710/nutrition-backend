package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.User; // Nếu bạn có entity User
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.UserRepository; // Nếu có
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserAdminController {

    @Autowired
    private HealthProfileRepository profileRepo;

    // Nếu bạn có entity User riêng (khuyến khích)
    // @Autowired
    // private UserRepository userRepo;

    // 1. Lấy danh sách tất cả HealthProfile (tức là tất cả người dùng có profile)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUserProfiles() {
        List<HealthProfile> profiles = profileRepo.findAll();

        List<Map<String, Object>> response = new ArrayList<>();
        for (HealthProfile p : profiles) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", p.getUserId());
            map.put("hasDiabetes", p.isHasDiabetes());
            map.put("hba1c", p.getHba1c());
            map.put("hasHypertension", p.isHasHypertension());
            map.put("bloodPressureSystolic", p.getBloodPressureSystolic());
            map.put("bloodPressureDiastolic", p.getBloodPressureDiastolic());
            map.put("hasCardiovascular", p.isHasCardiovascular());
            map.put("weightKg", p.getWeightKg());
            map.put("heightCm", p.getHeightCm());
            map.put("age", p.getAge());
            map.put("gender", p.getGender());
            map.put("dailyCalorieLimit", p.getDailyCalorieLimit());
            map.put("weightGoal", p.getWeightGoal() != null ? p.getWeightGoal().name() : "MAINTAIN");

            if (p.getDisease() != null) {
                map.put("diseaseName", p.getDisease().getDiseaseName());
                map.put("diseaseVietName", p.getDisease().getVietName());
            }

            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    // 2. Lấy profile chi tiết của 1 user theo userId
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        Optional<HealthProfile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HealthProfile profile = profileOpt.get();

        // Trả JSON sạch, tránh lỗi proxy
        Map<String, Object> response = new HashMap<>();
        response.put("userId", profile.getUserId());
        response.put("hasDiabetes", profile.isHasDiabetes());
        response.put("hba1c", profile.getHba1c());
        response.put("hasHypertension", profile.isHasHypertension());
        response.put("bloodPressureSystolic", profile.getBloodPressureSystolic());
        response.put("bloodPressureDiastolic", profile.getBloodPressureDiastolic());
        response.put("hasCardiovascular", profile.isHasCardiovascular());
        response.put("weightKg", profile.getWeightKg());
        response.put("heightCm", profile.getHeightCm());
        response.put("age", profile.getAge());
        response.put("gender", profile.getGender());
        response.put("dailyCalorieLimit", profile.getDailyCalorieLimit());
        response.put("weightGoal", profile.getWeightGoal() != null ? profile.getWeightGoal().name() : "MAINTAIN");

        if (profile.getDisease() != null) {
            response.put("diseaseName", profile.getDisease().getDiseaseName());
            response.put("diseaseVietName", profile.getDisease().getVietName());
        }

        return ResponseEntity.ok(getProfileMap(profile));
    }

    // 3. Sửa profile của user (admin chỉnh giúp)
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @RequestBody HealthProfile input) {
        HealthProfile existing = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile của userId: " + userId));

        // Cập nhật các trường (tương tự như trước)
        if (input.getHba1c() != null) existing.setHba1c(input.getHba1c());
        if (input.getBloodPressureSystolic() != null) existing.setBloodPressureSystolic(input.getBloodPressureSystolic());
        if (input.getBloodPressureDiastolic() != null) existing.setBloodPressureDiastolic(input.getBloodPressureDiastolic());
        if (input.getCholesterolTotal() != null) existing.setCholesterolTotal(input.getCholesterolTotal());
        if (input.getWeightKg() != null) existing.setWeightKg(input.getWeightKg());
        if (input.getHeightCm() != null) existing.setHeightCm(input.getHeightCm());
        if (input.getAge() != null) existing.setAge(input.getAge());
        if (input.getGender() != null) existing.setGender(input.getGender());
        existing.setHasDiabetes(input.isHasDiabetes());
        existing.setHasHypertension(input.isHasHypertension());
        existing.setHasCardiovascular(input.isHasCardiovascular());
        if (input.getWeightGoal() != null) existing.setWeightGoal(input.getWeightGoal());

        // Cập nhật disease nếu có
        if (input.getDisease() != null && input.getDisease().getId() != null) {
            existing.setDisease(input.getDisease());
        }

        HealthProfile updated = profileRepo.save(existing);

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật profile user " + userId + " thành công!",
                "profile", getProfileMap(updated)
        ));
    }

    // Helper: Trả map sạch tránh proxy
    private Map<String, Object> getProfileMap(HealthProfile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", profile.getUserId());
        map.put("hasDiabetes", profile.isHasDiabetes());
        map.put("hba1c", profile.getHba1c());
        map.put("hasHypertension", profile.isHasHypertension());
        map.put("weightKg", profile.getWeightKg());
        map.put("heightCm", profile.getHeightCm());
        map.put("dailyCalorieLimit", profile.getDailyCalorieLimit());
        map.put("weightGoal", profile.getWeightGoal() != null ? profile.getWeightGoal().name() : "MAINTAIN");
        if (profile.getDisease() != null) {
            map.put("diseaseName", profile.getDisease().getDiseaseName());
        }
        return map;
    }

}