package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.dto.StatisticsAdvice;
import com.example.nutrition_backend.service.NutritionAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    @Autowired
    private NutritionAdvisorService advisorService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsAdvice> getStatistics(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        StatisticsAdvice stats = advisorService.getStatisticsAdvice(userId, fromDate, toDate);
        return ResponseEntity.ok(stats);
    }
}