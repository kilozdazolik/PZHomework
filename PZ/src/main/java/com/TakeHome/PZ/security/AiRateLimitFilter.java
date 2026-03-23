package com.TakeHome.PZ.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AiRateLimitFilter extends OncePerRequestFilter {

    private final AiRateLimiterService aiRateLimiterService;
    private final ObjectMapper objectMapper;

    public AiRateLimitFilter(AiRateLimiterService aiRateLimiterService, ObjectMapper objectMapper) {
        this.aiRateLimiterService = aiRateLimiterService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/ai/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String userKey = extractUserKey(request);

        if (!aiRateLimiterService.tryConsume(userKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(new RateLimitBody(
                    "Too Many Requests",
                    "AI rate limit exceeded. Max 10 requests per hour per user."
            )));
            return;
        }

        response.setHeader("X-AI-RateLimit-Remaining", String.valueOf(aiRateLimiterService.remainingTokens(userKey)));
        filterChain.doFilter(request, response);
    }

    private static String extractUserKey(HttpServletRequest request) {
        String fromHeader = request.getHeader("X-User-Id");
        if (fromHeader != null && !fromHeader.isBlank()) {
            return "user:" + fromHeader.trim();
        }

        String fromQuery = request.getParameter("userId");
        if (fromQuery != null && !fromQuery.isBlank()) {
            return "user:" + fromQuery.trim();
        }

        String remote = request.getRemoteAddr();
        return "ip:" + (remote == null ? "unknown" : remote);
    }

    private record RateLimitBody(String message, String details) {}
}
