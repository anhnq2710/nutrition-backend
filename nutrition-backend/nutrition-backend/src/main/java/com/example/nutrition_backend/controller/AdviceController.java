package com.example.nutrition_backend.controller;



import com.example.nutrition_backend.dto.DailyAdvice;
import com.example.nutrition_backend.service.NutritionAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdviceController {

    @Autowired
    private NutritionAdvisorService advisorService;

    @GetMapping("/advice/daily")
    public DailyAdvice getDailyAdvice(@RequestParam String userId) {
        return advisorService.getDailyAdvice(userId);
    }
}