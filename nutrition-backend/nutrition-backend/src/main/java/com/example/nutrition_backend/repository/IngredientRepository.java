package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<FoodEntity, Long> {
    List<FoodEntity> findByNameContainingIgnoreCase(String name);
}
