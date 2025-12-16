package com.example.nutrition_backend.service;

import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    // Lấy tất cả nguyên liệu
    public List<FoodEntity> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    // Tìm nguyên liệu theo tên
    public List<FoodEntity> searchIngredients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllIngredients();
        }
        return ingredientRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    // Lấy 1 nguyên liệu theo ID (dùng khi tạo món)
    public FoodEntity getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu ID: " + id));
    }
}