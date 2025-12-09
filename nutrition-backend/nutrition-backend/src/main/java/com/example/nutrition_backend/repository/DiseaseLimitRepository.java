package com.example.nutrition_backend.repository;

import com.example.nutrition_backend.entity.DiseaseLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseLimitRepository extends JpaRepository<DiseaseLimit, Long> {
    Optional<DiseaseLimit> findByDiseaseName(String diseaseName);  // Tìm theo tên bệnh
}