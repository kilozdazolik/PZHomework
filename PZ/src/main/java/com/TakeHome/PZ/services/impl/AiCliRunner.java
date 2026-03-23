package com.TakeHome.PZ.services.impl;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.TakeHome.PZ.dto.AiCommandResponseDTO;
import com.TakeHome.PZ.services.AiCommandService;

@Component
@ConditionalOnProperty(prefix = "app.cli", name = "enabled", havingValue = "true")
public class AiCliRunner implements CommandLineRunner {

    private final AiCommandService aiCommandService;

    public AiCliRunner(AiCommandService aiCommandService) {
        this.aiCommandService = aiCommandService;
    }

    @Override
    public void run(String... args) {
        System.out.println("[AI CLI] Termeszetes nyelvu parancsmodul aktiv.");
        System.out.println("[AI CLI] Kilepeshez: exit");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("[AI CLI] User ID (UUID): ");
            String userId = scanner.nextLine().trim();

            while (true) {
                System.out.print("[AI CLI] > ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String prompt = scanner.nextLine().trim();
                if (prompt.equalsIgnoreCase("exit") || prompt.equalsIgnoreCase("kilep")) {
                    break;
                }

                AiCommandResponseDTO response = aiCommandService.handlePrompt(userId, prompt, true);
                System.out.println("[AI CLI] Command: " + response.getCommand());
                System.out.println("[AI CLI] Message: " + response.getMessage());
                if (response.isNeedsClarification() && response.getClarificationQuestion() != null) {
                    System.out.println("[AI CLI] Clarification: " + response.getClarificationQuestion());
                }
            }
        }

        System.out.println("[AI CLI] Leallitva.");
    }
}
