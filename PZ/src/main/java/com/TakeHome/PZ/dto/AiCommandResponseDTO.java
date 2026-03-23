package com.TakeHome.PZ.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiCommandResponseDTO {
    private String command;
    private String message;
    private boolean needsClarification;
    private String clarificationQuestion;

    public static AiCommandResponseDTO clarificationNeeded(String message, String question) {
        return new AiCommandResponseDTO("UNKNOWN", message, true, question);
    }

    public static AiCommandResponseDTO success(String command, String message) {
        return new AiCommandResponseDTO(command, message, false, null);
    }
}
