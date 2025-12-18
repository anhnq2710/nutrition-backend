package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.entity.User; // Nếu bạn có entity User
import com.example.nutrition_backend.repository.HealthProfileRepository;
import com.example.nutrition_backend.repository.UserRepository; // Nếu có
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private HealthProfileRepository profileRepo;

    // Lấy danh sách tất cả HealthProfile
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        Pageable pageable = PageRequest.of(page, size);

        Page<HealthProfile> profilePage = profileRepo.findAll(pageable);


        List<Map<String, Object>> userList = profilePage.getContent().stream()
                .map(this::getProfileMap)
                .collect(Collectors.toList());

        // Tạo response với thông tin phân trang
        Map<String, Object> response = new HashMap<>();
        response.put("users", userList);
        response.put("currentPage", profilePage.getNumber());
        response.put("pageSize", profilePage.getSize());
        response.put("totalItems", profilePage.getTotalElements());
        response.put("totalPages", profilePage.getTotalPages());
        response.put("hasNext", profilePage.hasNext());
        response.put("hasPrevious", profilePage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    // Lấy profile chi tiết của 1 user theo userId
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

    // Sửa profile của user (admin chỉnh giúp)
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @RequestBody HealthProfile input) {
        HealthProfile existing = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile của userId: " + userId));

        // Cập nhật các trường
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