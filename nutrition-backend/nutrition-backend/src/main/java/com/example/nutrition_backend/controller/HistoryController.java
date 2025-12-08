package com.example.nutrition_backend.controller;


import com.example.nutrition_backend.entity.MealHistory;
import com.example.nutrition_backend.service.MealHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HistoryController {

    @Autowired
    private MealHistoryService mealService;

    @PostMapping("/history/save")
    public MealHistory save(@RequestParam String userId, @RequestParam String foodName,
                            @RequestParam Double calories, @RequestParam Double protein,
                            @RequestParam Double fat, @RequestParam Double carbs,
                            @RequestParam Double sugar, @RequestParam Double sodium) {
        return mealService.saveMeal(userId, foodName, calories, protein, fat, carbs, sugar, sodium);
    }

    @GetMapping("/history/{userId}")
    public List<MealHistory> getHistory(@PathVariable String userId) {
        return mealService.getHistoryByUser(userId);
    }
}