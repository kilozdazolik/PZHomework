package com.TakeHome.PZ.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.TakeHome.PZ.dto.AiCommandResponseDTO;
import com.TakeHome.PZ.security.AiRateLimiterService;

@Component
public class AiPromptRequestValidator {

    private final AiRateLimiterService aiRateLimiterService;

    public AiPromptRequestValidator(AiRateLimiterService aiRateLimiterService) {
        this.aiRateLimiterService = aiRateLimiterService;
    }

    public ValidationResult validate(String userId, String prompt, boolean enforceRateLimit) {
        if (userId == null || userId.isBlank()) {
            return ValidationResult.error(AiCommandResponseDTO.clarificationNeeded(
                    "Hianyzik a felhasznalo azonosito.",
                    "Add meg a userId-t."
            ));
        }

        if (prompt == null || prompt.isBlank()) {
            return ValidationResult.error(AiCommandResponseDTO.clarificationNeeded(
                    "Hianyzik a prompt.",
                    "Ird le, mit szeretnel csinalni."
            ));
        }

        if (enforceRateLimit && !aiRateLimiterService.tryConsume("user:" + userId.trim())) {
            return ValidationResult.error(AiCommandResponseDTO.clarificationNeeded(
                    "Tul sok AI keres. Maximum 10 keres/orankent/felhasznalo.",
                    "Probald ujra kesobb."
            ));
        }

        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId.trim());
        } catch (IllegalArgumentException ex) {
            return ValidationResult.error(AiCommandResponseDTO.clarificationNeeded(
                    "Ervenytelen userId formatum.",
                    "Add meg ervenyes UUID-t."
            ));
        }

        return ValidationResult.ok(userUuid);
    }

    public record ValidationResult(UUID userId, AiCommandResponseDTO error) {
        static ValidationResult ok(UUID userId) {
            return new ValidationResult(userId, null);
        }

        static ValidationResult error(AiCommandResponseDTO error) {
            return new ValidationResult(null, error);
        }

        public boolean hasError() {
            return error != null;
        }
    }
}
