package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.CustomRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRecipeRepository extends JpaRepository<CustomRecipe, Long> {
    List<CustomRecipe> findByIsPublicTrueOrderByCreatedAtDesc();
    List<CustomRecipe> findByUserIdOrderByCreatedAtDesc(String userId);
}
