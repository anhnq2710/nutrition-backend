package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.CustomRecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRecipeIngredientRepository extends JpaRepository<CustomRecipeIngredient, Long> {
    List<CustomRecipeIngredient> findByRecipeId(Long recipeId);
}