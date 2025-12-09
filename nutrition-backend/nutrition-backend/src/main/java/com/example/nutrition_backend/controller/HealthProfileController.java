package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // CẬP NHẬT
        if (input.isHasDiabetes() != existing.isHasDiabetes()) {
            existing.setHasDiabetes(input.isHasDiabetes());
        }
        if (input.getHba1c() != null) existing.setHba1c(input.getHba1c());

        if (input.isHasHypertension() != existing.isHasHypertension()) {
            existing.setHasHypertension(input.isHasHypertension());
        }
        if (input.getBloodPressureSystolic() != null) existing.setBloodPressureSystolic(input.getBloodPressureSystolic());
        if (input.getBloodPressureDiastolic() != null) existing.setBloodPressureDiastolic(input.getBloodPressureDiastolic());

        if (input.isHasCardiovascular() != existing.isHasCardiovascular()) {
            existing.setHasCardiovascular(input.isHasCardiovascular());
        }
        if (input.getCholesterolTotal() != null) existing.setCholesterolTotal(input.getCholesterolTotal());

        if (input.getWeightKg() != null) existing.setWeightKg(input.getWeightKg());
        if (input.getHeightCm() != null) existing.setHeightCm(input.getHeightCm());
        if (input.getAge() != null) existing.setAge(input.getAge());
        if (input.getGender() != null && !input.getGender().isBlank()) existing.setGender(input.getGender());

        // Cập nhật disease nếu có disease_id
        if (input.getDisease() != null && input.getDisease().getId() != null) {
            diseaseLimitRepo.findById(input.getDisease().getId())
                    .ifPresent(existing::setDisease);
        }

        HealthProfile updated = profileRepo.save(existing);
        return ResponseEntity.ok(updated);
    }

    // PROFILE HIỂN THỊ
    @GetMapping
    public ResponseEntity<HealthProfile> getProfile(@RequestParam String userId) {
        return profileRepo.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}