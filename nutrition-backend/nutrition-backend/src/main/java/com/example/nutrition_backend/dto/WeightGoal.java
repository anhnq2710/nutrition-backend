package com.example.nutrition_backend.dto;

public enum WeightGoal {
    LOSE("Giảm cân"),
    MAINTAIN("Giữ cân"),
    GAIN("Tăng cân");

    private final String description;

    WeightGoal(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Để parse từ string (nếu FE gửi "lose")
    public static WeightGoal fromString(String text) {
        if (text == null) return null;
        for (WeightGoal g : WeightGoal.values()) {
            if (text.equalsIgnoreCase(g.name())) {
                return g;
            }
        }
        return null;
    }
}