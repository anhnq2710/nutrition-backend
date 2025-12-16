package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.WeightGoal;
import com.example.nutrition_backend.entity.DiseaseLimit;
import com.example.nutrition_backend.entity.FoodEntity;
import com.example.nutrition_backend.entity.HealthProfile;
import com.example.nutrition_backend.repository.DiseaseLimitRepository;
import com.example.nutrition_backend.repository.FoodRepository;
import com.example.nutrition_backend.repository.HealthProfileRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private final FoodRepository foodRepo;
    private final NutritionAdvisorService advisorService;
    private final DiseaseLimitRepository diseaseLimitRepo;
    private final HealthProfileRepository healthProfileRepo;

    // Pattern để xóa dấu
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public NutritionService(FoodRepository foodRepo,
                            NutritionAdvisorService advisorService,
                            DiseaseLimitRepository diseaseLimitRepo,
                            HealthProfileRepository healthProfileRepo) {
        this.foodRepo = foodRepo;
        this.advisorService = advisorService;
        this.diseaseLimitRepo = diseaseLimitRepo;
        this.healthProfileRepo = healthProfileRepo;
    }

    // loại dấu
    private static String stripDiacritics(String s) {
        if (s == null) return "";
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return DIACRITICS_PATTERN.matcher(normalized).replaceAll("").toLowerCase().trim();
    }

    // TÌM MÓN ĂN
    public Map<String, Object> searchNutrition(String name, String userId) {
        Map<String, Object> response = new HashMap<>();

        if (name == null || name.isBlank()) {
            response.put("foods", Collections.emptyList());
            response.put("message", "Parameter 'name' is empty");
            return response;
        }

        List<FoodEntity> candidates;
        try {
            candidates = foodRepo.searchByName(name);
        } catch (Exception ex) {
            candidates = Collections.emptyList();
        }

        if (candidates == null || candidates.isEmpty()) {
            candidates = foodRepo.findAll();
        }

        String qNorm = stripDiacritics(name);

        List<FoodEntity> filtered = candidates.stream()
                .filter(f -> stripDiacritics(f.getName()).contains(qNorm))
                .collect(Collectors.toList());

        response.put("foods", filtered);

        if (filtered.isEmpty()) {
            response.put("message", "Không tìm thấy món ăn!");
            return response;
        }

        if (userId != null && !userId.isBlank() && advisorService != null) {
            FoodEntity food = filtered.get(0);
            try {
                Map<String, Object> advice = advisorService.getAdviceForFood(userId, food);
                response.put("advice", advice);
            } catch (Exception ex) {
                response.put("adviceError", "Không thể lấy lời khuyên cá nhân hóa");
            }
        }

        return response;
    }

    // GỢI Ý TỪ KHÓA
    public List<Map<String, Object>> suggestNutrition(String query, int limit) {
        if (query == null) return Collections.emptyList();
        String q = query.trim();
        if (q.isEmpty()) return Collections.emptyList();
        if (limit <= 0) limit = 10;

        String qNorm = stripDiacritics(q);

        List<FoodEntity> allFoods = foodRepo.findAll();
        List<String> allNames = allFoods.stream()
                .map(FoodEntity::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toList());

        List<Map<String, Object>> results = new ArrayList<>();

        // 1) chính xác
        for (String s : allNames) {
            if (results.size() >= limit) break;
            if (stripDiacritics(s).equals(qNorm)) {
                results.add(Map.of("name", s, "matchType", "exact"));
            }
        }
        if (results.size() >= limit) return results;

        // 2) prefix
        for (String s : allNames) {
            if (results.size() >= limit) break;
            if (stripDiacritics(s).startsWith(qNorm) && results.stream().noneMatch(m -> m.get("name").equals(s))) {
                results.add(Map.of("name", s, "matchType", "prefix"));
            }
        }
        if (results.size() >= limit) return results;

        // 3) contains
        for (String s : allNames) {
            if (results.size() >= limit) break;
            if (stripDiacritics(s).contains(qNorm) && results.stream().noneMatch(m -> m.get("name").equals(s))) {
                results.add(Map.of("name", s, "matchType", "contains"));
            }
        }
        if (results.size() >= limit) return results;

        // 4) fuzzy
        List<LabelDist> dists = new ArrayList<>();
        for (String s : allNames) {
            if (results.stream().anyMatch(m -> m.get("name").equals(s))) continue;
            int dist = levenshteinDistance(qNorm, stripDiacritics(s));
            dists.add(new LabelDist(s, dist));
        }
        dists.sort(Comparator.comparingInt(ld -> ld.dist));
        for (LabelDist ld : dists) {
            if (results.size() >= limit) break;
            results.add(Map.of("name", ld.label, "matchType", "fuzzy", "distance", ld.dist));
        }

        return results;
    }

    // LẤY MÓN ĐẦU
    public Optional<FoodEntity> getNutritionByName(String name) {
        List<FoodEntity> results = foodRepo.searchByName(name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Helper cho fuzzy
    private static class LabelDist {
        String label;
        int dist;
        LabelDist(String label, int dist) {
            this.label = label;
            this.dist = dist;
        }
    }

    // Levenshtein distance
    private static int levenshteinDistance(String a, String b) {
        int n = a.length();
        int m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;

        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];

        for (int j = 0; j <= m; j++) prev[j] = j;

        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev;
            prev = cur;
            cur = tmp;
        }
        return prev[m];
    }

    // RECOMMEND FOR USER
    public List<Map<String, Object>> recommendForUser(String userId, int limit) {
        if (userId == null || userId.isBlank()) return Collections.emptyList();
        if (limit <= 0) limit = 5;

        Optional<HealthProfile> opt = healthProfileRepo.findByUserId(userId);
        if (opt.isEmpty()) return Collections.emptyList();
        HealthProfile profile = opt.get();

        WeightGoal goal = profile.getWeightGoal();
        if (goal == null) {
            goal = determineWeightGoalFromBMI(profile);
        }

        DiseaseLimit dl = profile.getDisease() != null ? profile.getDisease() : createTempDiseaseLimit(profile);

        List<FoodEntity> allFoods = foodRepo.findAll();
        if (allFoods.isEmpty()) return Collections.emptyList();

        List<ScoredFood> scored = new ArrayList<>();
        double dailyCalNeed = profile.getDailyCalorieLimit();

        for (FoodEntity f : allFoods) {
            if (f == null) continue;
            double score = 0.0;
            List<String> reasons = new ArrayList<>();

            // Tính chỉ số thực tế cho serving
            double multiplier = f.getServingMultiplier() != null ? f.getServingMultiplier() : 1.0;
            double actualCalories = f.getCalories() * multiplier;
            double actualProtein = f.getProtein() != null ? f.getProtein() * multiplier : 0;
            double actualCarbs = f.getCarbs() != null ? f.getCarbs() * multiplier : 0;
            double actualFat = f.getFat() != null ? f.getFat() * multiplier : 0;
            double actualSugar = f.getSugar() != null ? f.getSugar() * multiplier : 0;
            double actualSodium = f.getSodium() != null ? f.getSodium() * multiplier : 0;
            double actualFiber = f.getFiber() != null ? f.getFiber() * multiplier : 0;

            // PHẦN BỆNH LÝ – SO SÁNH THEO SERVING THỰC TẾ
            if (dl.getSugarMax() != null && actualSugar > 0) {
                if (actualSugar <= dl.getSugarMax()) {
                    score += 35;
                    reasons.add("đường dưới ngưỡng – tốt cho tiểu đường");
                } else {
                    double excess = actualSugar - dl.getSugarMax();
                    score -= excess * 2;
                    reasons.add("đường vượt ngưỡng");
                }
            }

            if (dl.getSodiumMax() != null && actualSodium > 0) {
                if (actualSodium <= dl.getSodiumMax()) {
                    score += 35;
                    reasons.add("muối dưới ngưỡng – tốt cho huyết áp");
                } else {
                    double excess = actualSodium - dl.getSodiumMax();
                    score -= excess * 0.05;
                    reasons.add("muối vượt ngưỡng");
                }
            }

            if (dl.getFatMax() != null && actualFat > 0) {
                if (actualFat <= dl.getFatMax()) {
                    score += 25;
                    reasons.add("chất béo dưới ngưỡng");
                } else {
                    double excess = actualFat - dl.getFatMax();
                    score -= excess * 1.5;
                    reasons.add("chất béo vượt ngưỡng");
                }
            }

            // PHẦN CÂN NẶNG – SO SÁNH THEO SERVING THỰC TẾ
            switch (goal) {
                case LOSE -> {
                    if (actualCalories < 200) {
                        score += 60;
                        reasons.add("rất ít calo – lý tưởng giảm cân");
                    } else if (actualCalories < 300) {
                        score += 40;
                        reasons.add("ít calo – phù hợp giảm cân");
                    }

                    if (actualProtein > 15) {
                        score += 35;
                        reasons.add("nhiều protein – giữ cơ khi giảm mỡ");
                    }

                    if (actualFiber > 3) {
                        score += 25;
                        reasons.add("nhiều chất xơ – no lâu");
                    }
                }

                case GAIN -> {
                    if (actualCalories > 400) {
                        score += 60;
                        reasons.add("nhiều calo – lý tưởng tăng cân lành mạnh");
                    } else if (actualCalories > 250) {
                        score += 40;
                        reasons.add("calo tốt – hỗ trợ tăng cân");
                    }

                    if (actualProtein > 20) {
                        score += 45;
                        reasons.add("nhiều protein – tăng cơ bắp");
                    }

                    if (actualCarbs > 30) {
                        score += 25;
                        reasons.add("nhiều carbs – năng lượng dồi dào");
                    }
                }

                case MAINTAIN -> {
                    double target = dailyCalNeed / 3;
                    double diff = Math.abs(actualCalories - target);
                    if (diff < 100) {
                        score += 60;
                        reasons.add("calo cân bằng – lý tưởng giữ cân");
                    } else if (diff < 200) {
                        score += 35;
                        reasons.add("calo gần cân bằng");
                    }

                    if (actualProtein > 15 && actualProtein < 35) {
                        score += 25;
                        reasons.add("protein phù hợp duy trì cơ");
                    }
                }
            }

            // Bonus chung
            String combined = stripDiacritics((f.getName() + " " + (f.getNote() == null ? "" : f.getNote())).toLowerCase());
            if (combined.contains("healthy") || combined.contains("salad") || combined.contains("grilled") || combined.contains("hấp") || combined.contains("luộc")) {
                score += 20;
                reasons.add("món lành mạnh");
            }

            scored.add(new ScoredFood(f, score, reasons));
        }

        // Sort + top limit
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, scored.size()); i++) {
            ScoredFood sf = scored.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("food", sf.food);
            item.put("score", Math.round(sf.score * 10) / 10.0);
            item.put("reasons", sf.reasons);
            item.put("goal", goal.getDescription());
            result.add(item);
        }

        return result;
    }

    // Helper: Tự động xác định từ BMI
    private WeightGoal determineWeightGoalFromBMI(HealthProfile profile) {
        if (profile.getWeightKg() == null || profile.getHeightCm() == null) return WeightGoal.MAINTAIN;

        double heightM = profile.getHeightCm() / 100.0;
        double bmi = profile.getWeightKg() / (heightM * heightM);

        if (bmi < 18.5) return WeightGoal.GAIN;
        if (bmi > 25) return WeightGoal.LOSE;
        return WeightGoal.MAINTAIN;
    }

    // Helper: Tạo DiseaseLimit tạm
    private DiseaseLimit createTempDiseaseLimit(HealthProfile profile) {
        DiseaseLimit temp = new DiseaseLimit();

        if (profile.isHasDiabetes()) {
            temp.setSugarMax(25.0);
        }
        if (profile.isHasHypertension()) {
            temp.setSodiumMax(600.0);
        }
        if (profile.isHasCardiovascular()) {
            temp.setFatMax(20.0);
        }

        temp.setCalorieMax(profile.getDailyCalorieLimit());
        temp.setDiseaseName("temp_profile_based");
        temp.setVietName("Dựa trên hồ sơ cá nhân");

        return temp;
    }

    // ScoredFood helper
    private static class ScoredFood {
        FoodEntity food;
        double score;
        List<String> reasons;
        ScoredFood(FoodEntity food, double score, List<String> reasons) {
            this.food = food;
            this.score = score;
            this.reasons = reasons;
        }
    }
}