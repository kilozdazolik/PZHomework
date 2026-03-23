package com.TakeHome.PZ.services;

import com.TakeHome.PZ.dto.AiCommandResponseDTO;

public interface AiCommandService {
    AiCommandResponseDTO handlePrompt(String userId, String prompt, boolean enforceRateLimit);
}
