package com.example.nutrition_backend.service;

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
                            NutritionAdvisorService advisorService, DiseaseLimitRepository diseaseLimitRepo, HealthProfileRepository healthProfileRepo) {
        this.foodRepo = foodRepo;
        this.advisorService = advisorService;
        this.diseaseLimitRepo = diseaseLimitRepo;
        this.healthProfileRepo = healthProfileRepo;
    }

    // loại dấu
    private static String stripDiacritics(String s) {
        if (s == null) return "";
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICS_PATTERN.matcher(normalized).replaceAll("");
        return withoutDiacritics.toLowerCase().trim();
    }

    // TÌM MÓN ĂN
    public Map<String, Object> searchNutrition(String name, String userId) {
        Map<String, Object> response = new HashMap<>();

        if (name == null || name.isBlank()) {
            response.put("foods", Collections.emptyList());
            response.put("message", "Parameter 'name' is empty");
            return response;
        }

        // Lấy candidate từ repo
        List<FoodEntity> candidates;
        try {
            candidates = foodRepo.searchByName(name);
        } catch (Exception ex) {
            candidates = Collections.emptyList();
        }

        // Nếu repo trả rỗng -> fallback lấy tất cả để áp dụng tìm không dấu
        if (candidates == null || candidates.isEmpty()) {
            candidates = foodRepo.findAll();
        }

        String qNorm = stripDiacritics(name);

        // Lọc lại bằng normalized compare để hỗ trợ tìm không dấu
        List<FoodEntity> filtered = candidates.stream()
                .filter(f -> {
                    String n = stripDiacritics(f.getName());
                    return n.contains(qNorm);
                })
                .collect(Collectors.toList());

        response.put("foods", filtered);

        if (filtered.isEmpty()) {
            response.put("message", "Không tìm thấy món ăn!");
            return response;
        }

        // Nếu cần advice (cá nhân hoá) khi có userId
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

        // Lấy toàn bộ tên món
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

        // 2) đoán
        for (String s : allNames) {
            if (results.size() >= limit) break;
            if (stripDiacritics(s).startsWith(qNorm) && results.stream().noneMatch(m -> m.get("name").equals(s))) {
                results.add(Map.of("name", s, "matchType", "prefix"));
            }
        }
        if (results.size() >= limit) return results;

        // 3) chứa
        for (String s : allNames) {
            if (results.size() >= limit) break;
            if (stripDiacritics(s).contains(qNorm) && results.stream().noneMatch(m -> m.get("name").equals(s))) {
                results.add(Map.of("name", s, "matchType", "contains"));
            }
        }
        if (results.size() >= limit) return results;

        // 4) fuzzy (Levenshtein) on normalized strings tìm kiếm bằng thuật toán Levenshtein
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


    // ĐỀ XUẤT MÓN ĂN DỰA THEO BỆNH
    /**
     * Gợi ý món ăn cho user dựa trên HealthProfile (nếu profile.disease != null dùng DiseaseLimit,
     * nếu không có disease link -> suy ra từ flags hasDiabetes/hasHypertension/hasCardiovascular/weight -> tạo limit tạm)
     * Trả list tối đa 'limit' món, mỗi phần tử: { food, score, reasons }
     * LƯU Ý: Đây là gợi ý kỹ thuật, KHÔNG THAY THẾ TƯ VẤN Y TẾ.
     */
    public List<Map<String, Object>> recommendForUser(String userId, int limit) {
        if (userId == null || userId.isBlank()) return Collections.emptyList();
        if (limit <= 0) limit = 5;

        Optional<HealthProfile> opt = healthProfileRepo.findByUserId(userId);
        if (opt.isEmpty()) return Collections.emptyList();
        HealthProfile profile = opt.get();

        // nếu profile liên kết DiseaseLimit thì dùng nó
        if (profile.getDisease() != null) {
            return recommendByDiseaseLimit(profile.getDisease(), limit);
        }

        // nếu không có disease link -> xây DiseaseLimit tạm dựa trên flags trong profile
        DiseaseLimit temp = new DiseaseLimit();
        temp.setDiseaseName("profile_based_" + (profile.getUserId() == null ? "u" : profile.getUserId()));
        // set thresholds hợp lý (có thể thay đổi theo nhu cầu)
        if (profile.isHasDiabetes()) {
            temp.setSugarMax(25.0); // g per serving (ví dụ)
        }
        if (profile.isHasHypertension()) {
            temp.setSodiumMax(600.0); // mg per serving (ví dụ)
        }
        if (profile.isHasCardiovascular()) {
            temp.setFatMax(20.0);
        }
        // nếu user có cân nặng/height/age -> sử dụng daily calorie limit làm calorieMax
        double dailyCal = profile.getDailyCalorieLimit();
        temp.setCalorieMax(dailyCal);

        return recommendByDiseaseLimit(temp, limit);
    }

    // Helper: dùng DiseaseLimit để recommend (chung cho cả disease config và profile-based)
    private List<Map<String, Object>> recommendByDiseaseLimit(DiseaseLimit dl, int limit) {
        if (dl == null) return Collections.emptyList();
        if (limit <= 0) limit = 5;

        List<FoodEntity> all = foodRepo.findAll();
        if (all.isEmpty()) return Collections.emptyList();

        // compute maxima to normalize (avoid divide-by-zero)
        double maxSugar = 0, maxSodium = 0, maxFat = 0, maxCal = 0;
        for (FoodEntity f : all) {
            if (f == null) continue;
            if (f.getSugar() != null) maxSugar = Math.max(maxSugar, f.getSugar());
            if (f.getSodium() != null) maxSodium = Math.max(maxSodium, f.getSodium());
            if (f.getFat() != null) maxFat = Math.max(maxFat, f.getFat());
            if (f.getCalories() != null) maxCal = Math.max(maxCal, f.getCalories());
        }
        if (maxSugar == 0) maxSugar = 1.0;
        if (maxSodium == 0) maxSodium = 1.0;
        if (maxFat == 0) maxFat = 1.0;
        if (maxCal == 0) maxCal = 1.0;

        List<ScoredFood> scored = new ArrayList<>();
        String diseaseKey = stripDiacritics(dl.getDiseaseName() == null ? "" : dl.getDiseaseName()).toLowerCase();

        for (FoodEntity f : all) {
            if (f == null) continue;
            double score = 0;
            List<String> reasons = new ArrayList<>();

            // sugar
            if (dl.getSugarMax() != null && f.getSugar() != null) {
                if (f.getSugar() <= dl.getSugarMax()) {
                    score += 30; reasons.add("sugar below limit");
                } else {
                    double ratio = (f.getSugar() - dl.getSugarMax()) / Math.max(1.0, dl.getSugarMax());
                    score -= ratio * 20; reasons.add("sugar above limit");
                }
            }

            // sodium
            if (dl.getSodiumMax() != null && f.getSodium() != null) {
                if (f.getSodium() <= dl.getSodiumMax()) {
                    score += 30; reasons.add("sodium below limit");
                } else {
                    double ratio = (f.getSodium() - dl.getSodiumMax()) / Math.max(1.0, dl.getSodiumMax());
                    score -= ratio * 20; reasons.add("sodium above limit");
                }
            }

            // fat
            if (dl.getFatMax() != null && f.getFat() != null) {
                if (f.getFat() <= dl.getFatMax()) {
                    score += 20; reasons.add("fat below limit");
                } else {
                    double ratio = (f.getFat() - dl.getFatMax()) / Math.max(1.0, dl.getFatMax());
                    score -= ratio * 15; reasons.add("fat above limit");
                }
            }

            // calories
            if (dl.getCalorieMax() != null && f.getCalories() != null) {
                if (f.getCalories() <= dl.getCalorieMax()) {
                    score += 10; reasons.add("calories below limit");
                } else {
                    double ratio = (f.getCalories() - dl.getCalorieMax()) / Math.max(1.0, dl.getCalorieMax());
                    score -= ratio * 10; reasons.add("calories above limit");
                }
            }

            // text boost: nếu name/note chứa từ khóa diseaseName thì + bonus
            String combined = stripDiacritics((f.getName() == null ? "" : f.getName()) + " " +
                    (f.getNote() == null ? "" : f.getNote())).toLowerCase();
            if (!diseaseKey.isBlank() && combined.contains(diseaseKey)) {
                score += 20; reasons.add("text match disease keyword");
            }

            // small healthy boost
            if (combined.contains("salad") || combined.contains("low") || combined.contains("healthy") || combined.contains("fresh")) {
                score += 5; reasons.add("healthy tag");
            }

            scored.add(new ScoredFood(f, score, reasons));
        }

        // sort desc
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        // build top-k
        List<Map<String, Object>> out = new ArrayList<>();
        int cnt = 0;
        for (ScoredFood sf : scored) {
            if (cnt >= limit) break;
            Map<String, Object> item = new HashMap<>();
            item.put("food", sf.food);
            item.put("score", Math.round(sf.score * 100.0) / 100.0);
            item.put("reasons", sf.reasons);
            out.add(item);
            cnt++;
        }
        return out;
    }
    // ScoredFood helper
    private static class ScoredFood {
        FoodEntity food;
        double score;
        List<String> reasons;
        ScoredFood(FoodEntity food, double score, List<String> reasons) {
            this.food = food; this.score = score; this.reasons = reasons;
        }
    }
}
