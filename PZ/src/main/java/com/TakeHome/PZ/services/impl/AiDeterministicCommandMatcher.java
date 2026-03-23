package com.TakeHome.PZ.services.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.TakeHome.PZ.dto.AiDecisionDTO;
import com.TakeHome.PZ.dto.ApplicationResponseDTO;

@Component
public class AiDeterministicCommandMatcher {

    public static final String COMMAND_OPEN_APP = "OPEN_APP";
    public static final String COMMAND_LIST_APPS = "LIST_APPS";
    public static final String COMMAND_SET_THEME = "SET_THEME";
    public static final String COMMAND_UNKNOWN = "UNKNOWN";

    private static final String OPEN_APP_PREFIX = "open app ";
    private static final String LAUNCH_APP_PREFIX = "launch app ";

    private final AppNameNormalizer appNameNormalizer;

    public AiDeterministicCommandMatcher(AppNameNormalizer appNameNormalizer) {
        this.appNameNormalizer = appNameNormalizer;
    }

    public Optional<AiDecisionDTO> match(String prompt, List<ApplicationResponseDTO> apps) {
        String normalized = prompt.toLowerCase(Locale.ROOT).trim();

        if (normalized.equals("list apps")
                || normalized.equals("list applications")
                || normalized.equals("show apps")) {
            return Optional.of(commandOnly(COMMAND_LIST_APPS));
        }

        if (normalized.equals("open app")
                || normalized.equals("open an app")
                || normalized.equals("launch app")) {
            return Optional.of(AiDecisionDTO.unknown(
                    "Nem derult ki, melyik alkalmazast szeretned inditani.",
                    "Melyik alkalmazast inditsam el?"
            ));
        }

        if (normalized.startsWith(OPEN_APP_PREFIX)) {
            return Optional.of(openAppDecision(prompt.substring(OPEN_APP_PREFIX.length()).trim()));
        }

        if (normalized.startsWith(LAUNCH_APP_PREFIX)) {
            return Optional.of(openAppDecision(prompt.substring(LAUNCH_APP_PREFIX.length()).trim()));
        }

        if (!prompt.contains(" ")) {
            String resolved = appNameNormalizer.normalizeInput(prompt);
            if (!resolved.isBlank() && appExists(apps, resolved)) {
                return Optional.of(openAppDecision(resolved));
            }
        }

        return Optional.empty();
    }

    private AiDecisionDTO openAppDecision(String rawAppName) {
        AiDecisionDTO decision = new AiDecisionDTO();
        decision.setCommand(COMMAND_OPEN_APP);
        decision.getArguments().put("appName", appNameNormalizer.normalizeInput(rawAppName));
        return decision;
    }

    private AiDecisionDTO commandOnly(String command) {
        AiDecisionDTO decision = new AiDecisionDTO();
        decision.setCommand(command);
        return decision;
    }

    private boolean appExists(List<ApplicationResponseDTO> apps, String appName) {
        String normalizedInput = appNameNormalizer.normalizeComparable(appName);
        return apps.stream()
                .map(ApplicationResponseDTO::getName)
                .map(appNameNormalizer::normalizeComparable)
                .anyMatch(existing -> existing.equals(normalizedInput));
    }
}
