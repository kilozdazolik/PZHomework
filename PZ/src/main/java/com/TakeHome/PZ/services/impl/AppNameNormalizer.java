package com.TakeHome.PZ.services.impl;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class AppNameNormalizer {

    public String normalizeInput(String rawAppName) {
        if (rawAppName == null) {
            return "";
        }

        String normalized = rawAppName.trim();
        normalized = normalized.replaceFirst("(?i)^(app|application|name|neve|alkalmazas)\\s*[:=-]\\s*", "");
        normalized = normalized.replaceAll("^['\"`]+|['\"`]+$", "").trim();
        return normalized;
    }

    public String normalizeComparable(String rawName) {
        if (rawName == null) {
            return "";
        }

        String normalized = rawName.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized;
    }
}
