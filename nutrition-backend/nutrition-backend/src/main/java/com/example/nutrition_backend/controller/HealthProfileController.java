package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class HealthProfileController {

    @Autowired
    private HealthProfileRepository profileRepo;

    @Autowired
    private DiseaseLimitRepository diseaseLimitRepo;

    // TẠO PROFILE MỚI
    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody HealthProfile input) {
        if (input.getUserId() == null || input.getUserId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "userId là bắt buộc!"));
        }

        if (profileRepo.existsByUserId(input.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Profile đã tồn tại! Dùng PUT để cập nhật."));
        }

        // Nếu có disease_id → gán DiseaseLimit
        if (input.getDisease() != null && input.getDisease().getId() != null) {
            diseaseLimitRepo.findById(input.getDisease().getId())
                    .ifPresent(input::setDisease);
        }

        HealthProfile saved = profileRepo.save(input);
        return ResponseEntity.ok(saved);
    }

    // CẬP NHẬT PROFILE
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody HealthProfile input) {
        if (input.getUserId() == null || input.getUserId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "userId là bắt buộc!"));
        }

        HealthProfile existing = profileRepo.findByUserId(input.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile của userId: " + input.getUserId()));

        // CẬP NHẬT CÁC TRƯỜNG THÔNG THƯỜNG
        if (input.getHba1c() != null) existing.setHba1c(input.getHba1c());
        if (input.getBloodPressureSystolic() != null) existing.setBloodPressureSystolic(input.getBloodPressureSystolic());
        if (input.getBloodPressureDiastolic() != null) existing.setBloodPressureDiastolic(input.getBloodPressureDiastolic());
        if (input.getCholesterolTotal() != null) existing.setCholesterolTotal(input.getCholesterolTotal());
        if (input.getWeightKg() != null) existing.setWeightKg(input.getWeightKg());
        if (input.getHeightCm() != null) existing.setHeightCm(input.getHeightCm());
        if (input.getAge() != null) existing.setAge(input.getAge());
        if (input.getGender() != null && !input.getGender().isBlank()) existing.setGender(input.getGender());

        // Cập nhật flags bệnh
        existing.setHasDiabetes(input.isHasDiabetes());
        existing.setHasHypertension(input.isHasHypertension());
        existing.setHasCardiovascular(input.isHasCardiovascular());

        // Cập nhật disease nếu có disease_id từ input
        if (input.getDisease() != null && input.getDisease().getId() != null) {
            diseaseLimitRepo.findById(input.getDisease().getId())
                    .ifPresent(existing::setDisease);
        }

        // Lưu và cập nhật dailyCalorieLimit nếu cần (nếu bạn có logic tính BMR)
        HealthProfile updated = profileRepo.save(existing);

        // === TRẢ VỀ DTO AN TOÀN – KHÔNG CÓ PROXY ===
        Map<String, Object> response = new HashMap<>();
        response.put("userId", updated.getUserId());
        response.put("hasDiabetes", updated.isHasDiabetes());
        response.put("hba1c", updated.getHba1c());
        response.put("hasHypertension", updated.isHasHypertension());
        response.put("bloodPressureSystolic", updated.getBloodPressureSystolic());
        response.put("bloodPressureDiastolic", updated.getBloodPressureDiastolic());
        response.put("hasCardiovascular", updated.isHasCardiovascular());
        response.put("cholesterolTotal", updated.getCholesterolTotal());
        response.put("weightKg", updated.getWeightKg());
        response.put("heightCm", updated.getHeightCm());
        response.put("age", updated.getAge());
        response.put("gender", updated.getGender());
        response.put("dailyCalorieLimit", updated.getDailyCalorieLimit());
        response.put("weightGoal", updated.getWeightGoal() != null ? updated.getWeightGoal().name() : null);

        // Nếu có disease → chỉ trả tên hoặc id, không trả toàn bộ object
        if (updated.getDisease() != null) {
            Map<String, Object> diseaseInfo = new HashMap<>();
            diseaseInfo.put("id", updated.getDisease().getId());
            diseaseInfo.put("diseaseName", updated.getDisease().getDiseaseName());
            response.put("disease", diseaseInfo);
        } else {
            response.put("disease", null);
        }

        response.put("message", "Cập nhật profile thành công!");

        return ResponseEntity.ok(response);
    }

    // PROFILE HIỂN THỊ
    @GetMapping
    public ResponseEntity<HealthProfile> getProfile(@RequestParam String userId) {
        return profileRepo.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}