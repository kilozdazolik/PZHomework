package com.TakeHome.PZ.services.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.TakeHome.PZ.dto.AiDecisionDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class OpenAiClient {

    private static final String SYSTEM_PROMPT = """
            You are a strict command router for a family desktop app.
            You must only return JSON with this schema:
            {
              \"command\": \"OPEN_APP|LIST_APPS|SET_THEME|UNKNOWN\",
              \"arguments\": {
                \"appName\": \"optional string\",
                \"theme\": \"LIGHT or DARK\"
              },
              \"clarificationQuestion\": \"optional question when command is UNKNOWN or missing arguments\"
            }
            Rules:
            - Never return any command outside OPEN_APP, LIST_APPS, SET_THEME, UNKNOWN.
            - If user intent is unclear, return UNKNOWN with clarificationQuestion.
            - If required arguments are missing, return UNKNOWN with clarificationQuestion.
            - Return JSON only, no markdown, no explanation.
            """;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public OpenAiClient(
            ObjectMapper objectMapper,
            @Value("${openai.api.key:}") String apiKey,
            @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}") String apiUrl,
            @Value("${openai.api.model:gpt-4o-mini}") String model
    ) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public AiDecisionDTO mapPromptToCommand(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is missing. Configure OPENAI_API_KEY environment variable.");
        }

        try {
            String requestBody = buildRequestBody(userPrompt);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("LLM call failed with status: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            String contentText = objectMapper.convertValue(contentNode, String.class);
            if (contentNode.isMissingNode() || contentText == null || contentText.isBlank()) {
                return unknown("Nem ertheto a kerelmed. Pontositsd, mit szeretnel.");
            }

            String rawContent = stripMarkdownFences(contentText);
            AiDecisionDTO parsed = objectMapper.readValue(rawContent, AiDecisionDTO.class);
            if (parsed.getCommand() == null || parsed.getCommand().isBlank()) {
                return unknown("Nem egyertelmu, melyik parancsot szeretned futtatni.");
            }

            return parsed;
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Nem sikerult az LLM valasz feldolgozasa.");
        }
    }

    private String buildRequestBody(String userPrompt) throws IOException {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", 0);

        ArrayNode messages = root.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", SYSTEM_PROMPT);
        messages.addObject()
                .put("role", "user")
                .put("content", userPrompt);

        return objectMapper.writeValueAsString(root);
    }

    private static String stripMarkdownFences(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            String withoutPrefix = trimmed.replaceFirst("^```[a-zA-Z]*\\n", "");
            return withoutPrefix.substring(0, withoutPrefix.length() - 3).trim();
        }
        return trimmed;
    }

    private static AiDecisionDTO unknown(String clarification) {
        AiDecisionDTO dto = new AiDecisionDTO();
        dto.setCommand("UNKNOWN");
        dto.setClarificationQuestion(clarification);
        return dto;
    }
}
