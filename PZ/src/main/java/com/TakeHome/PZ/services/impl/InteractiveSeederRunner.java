package com.TakeHome.PZ.services.impl;

import com.TakeHome.PZ.dto.AiCommandResponseDTO;
import com.TakeHome.PZ.repository.UserRepository;
import com.TakeHome.PZ.services.AiCommandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;

@Component
public class InteractiveSeederRunner implements CommandLineRunner {
    private final AiCommandService aiCommandService;
    private final UserRepository userRepository;

    public InteractiveSeederRunner(
            UserRepository userRepository,
            AiCommandService aiCommandService
    ) {
        this.userRepository = userRepository;
        this.aiCommandService = aiCommandService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        UUID userId = promptValidUserId(scanner);
        boolean awaitingAppName = false;

        String userLabel = userRepository.findById(userId)
                .map(user -> user.getName())
                .orElse(userId.toString());

        System.out.println("LLM: Welcome " + userLabel + ", what do you want to do?");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String prompt = scanner.nextLine().trim();

            if (prompt.equalsIgnoreCase("exit")) {
                System.out.println("LLM: Bye.");
                return;
            }

            String normalizedPrompt = prompt.toLowerCase();
            if (awaitingAppName
                    && !normalizedPrompt.startsWith("open app")
                    && !normalizedPrompt.startsWith("launch app")) {
                prompt = "open app " + prompt;
            }

            AiCommandResponseDTO response = handlePrompt(userId, prompt);
            String clarification = response.getClarificationQuestion();
            awaitingAppName = response.isNeedsClarification()
                    && clarification != null
                    && (clarification.toLowerCase().contains("app") || clarification.toLowerCase().contains("alkalmaz"));
            System.out.println();
        }
    }

    private UUID promptValidUserId(Scanner scanner) {
        while (true) {
            System.out.print("User ID (UUID): ");
            String rawUserId = scanner.nextLine().trim();

            try {
                return UUID.fromString(rawUserId);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid user ID format. Please try again.");
            }
        }
    }

    private AiCommandResponseDTO handlePrompt(UUID userId, String prompt) {
        try {
            AiCommandResponseDTO response = aiCommandService.handlePrompt(userId.toString(), prompt, true);

            System.out.println("LLM: " + response.getMessage());
            if (response.isNeedsClarification() && response.getClarificationQuestion() != null) {
                System.out.println("LLM: " + response.getClarificationQuestion());
            }
            return response;
        } catch (RuntimeException ex) {
            System.out.println("LLM: Valami hiba tortent a feldolgozas kozben. Probald ujra.");
            return new AiCommandResponseDTO(
                    "UNKNOWN",
                    "Valami hiba tortent a feldolgozas kozben.",
                    false,
                    null
            );
        }
    }
}
