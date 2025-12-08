package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    List<FoodEntity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT f FROM FoodEntity f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FoodEntity> searchByName(@Param("name") String name);
}