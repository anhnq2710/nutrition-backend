package com.example.nutrition_backend.controller;

import com.example.nutrition_backend.service.PredictAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class PredictController {

    private static final Logger log = LoggerFactory.getLogger(PredictController.class);

    private final PredictAIService predictAIService;

    public PredictController(PredictAIService predictAIService) {
        this.predictAIService = predictAIService;
    }

    @PostMapping(path = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> predict(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "userId", required = false) String userId) {

        // Basic validation
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required and must not be empty");
        }

        // Optional: check content type (jpg/png) or size
        String contentType = image.getContentType();
        if (contentType == null || !(contentType.contains("jpeg") || contentType.contains("png"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG/PNG images are supported");
        }

        try {
            Map<String, Object> result = predictAIService.predict(image);

            if (userId != null && !userId.isBlank()) {
                result.put("userId", userId);
            }

            log.info("Prediction successful{}",
                    Objects.toString(result.get("best"), ""));
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException ex) {
            log.warn("Bad request while predicting: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Prediction failed", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Prediction failed", ex);
        }
    }
}
