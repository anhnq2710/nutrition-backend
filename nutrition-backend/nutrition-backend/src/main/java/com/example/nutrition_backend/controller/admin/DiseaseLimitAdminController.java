package com.example.nutrition_backend.controller.admin;

import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/disease-limits")
public class DiseaseLimitAdminController {

    @Autowired
    private DiseaseLimitRepository diseaseLimitRepo;

    // Lấy danh sách tất cả ngưỡng bệnh
    @GetMapping
    public ResponseEntity<List<DiseaseLimit>> getAllLimits() {
        return ResponseEntity.ok(diseaseLimitRepo.findAll());
    }

    // Thêm ngưỡng mới (ví dụ bệnh mới hoặc variant)
    @PostMapping
    public ResponseEntity<?> addLimit(@RequestBody DiseaseLimit input) {
        // Kiểm tra trùng diseaseName
        if (diseaseLimitRepo.findByDiseaseNameIgnoreCase(input.getDiseaseName()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bệnh '" + input.getDiseaseName() + "' đã tồn tại!"));
        }

        DiseaseLimit saved = diseaseLimitRepo.save(input);
        return ResponseEntity.ok(Map.of(
                "message", "Thêm ngưỡng bệnh thành công!",
                "limit", saved
        ));
    }

    // Sửa ngưỡng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLimit(@PathVariable Long id, @RequestBody DiseaseLimit input) {
        DiseaseLimit existing = diseaseLimitRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngưỡng ID: " + id));

        // Cập nhật các trường nếu có
        if (input.getDiseaseName() != null) existing.setDiseaseName(input.getDiseaseName());
        if (input.getVietName() != null) existing.setVietName(input.getVietName());
        if (input.getSugarMax() != null) existing.setSugarMax(input.getSugarMax());
        if (input.getSodiumMax() != null) existing.setSodiumMax(input.getSodiumMax());
        if (input.getFatMax() != null) existing.setFatMax(input.getFatMax());
        if (input.getCalorieMax() != null) existing.setCalorieMax(input.getCalorieMax());
        if (input.getNote() != null) existing.setNote(input.getNote());

        DiseaseLimit updated = diseaseLimitRepo.save(existing);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật ngưỡng thành công!",
                "limit", updated
        ));
    }

    // Xóa ngưỡng
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLimit(@PathVariable Long id) {
        if (!diseaseLimitRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy ngưỡng ID: " + id);
        }
        diseaseLimitRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa ngưỡng thành công!"));
    }

    // lấy bệnh theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getDiseaseLimitById(@PathVariable Long id) {
        return diseaseLimitRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}