package com.TakeHome.PZ.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class AiDecisionDTO {
    private String command;
    private String message;
    private Map<String, String> arguments = new HashMap<>();
    private String clarificationQuestion;

    public static AiDecisionDTO unknown(String message, String clarificationQuestion) {
        AiDecisionDTO dto = new AiDecisionDTO();
        dto.setCommand("UNKNOWN");
        dto.setMessage(message);
        dto.setClarificationQuestion(clarificationQuestion);
        return dto;
    }
}
