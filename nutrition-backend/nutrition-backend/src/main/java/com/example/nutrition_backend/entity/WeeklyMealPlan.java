package com.example.nutrition_backend.entity;

import com.example.nutrition_backend.dto.MealDay;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "weekly_meal_plan")
public class WeeklyMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "start_date") // Thứ 2 của tuần
    private LocalDate startDate;

    @ElementCollection
    @CollectionTable(name = "weekly_meal_plan_days", joinColumns = @JoinColumn(name = "plan_id"))
    private List<MealDay> days = new ArrayList<>();

    // Constructors, getters, setters
    public WeeklyMealPlan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public List<MealDay> getDays() { return days; }
    public void setDays(List<MealDay> days) { this.days = days; }
}
