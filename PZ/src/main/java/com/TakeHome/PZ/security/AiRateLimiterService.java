package com.TakeHome.PZ.security;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bucket;

@Service
public class AiRateLimiterService {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int windowHours;

    public AiRateLimiterService(
            @Value("${app.ai.rate-limit.capacity:10}") int capacity,
            @Value("${app.ai.rate-limit.window-hours:1}") int windowHours
    ) {
        this.capacity = capacity;
        this.windowHours = windowHours;
    }

    public boolean tryConsume(String key) {
        return getBucket(key).tryConsume(1);
    }

    public long remainingTokens(String key) {
        return getBucket(key).getAvailableTokens();
    }

    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    private Bucket newBucket(String ignoredKey) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(capacity)
                        .refillGreedy(capacity, Duration.ofHours(windowHours)))
                .build();
    }
}
