package com.TakeHome.PZ.services.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.TakeHome.PZ.dto.AiCommandResponseDTO;
import com.TakeHome.PZ.dto.AiDecisionDTO;
import com.TakeHome.PZ.dto.ApplicationResponseDTO;
import com.TakeHome.PZ.models.Enums.Theme;
import com.TakeHome.PZ.services.AiCommandService;
import com.TakeHome.PZ.services.ApplicationService;
import com.TakeHome.PZ.services.UserService;

@Service
public class AiCommandServiceImpl implements AiCommandService {

    private final OpenAiClient openAiClient;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final AiPromptRequestValidator requestValidator;
    private final AiDeterministicCommandMatcher deterministicMatcher;
    private final AppNameNormalizer appNameNormalizer;
    private final Map<String, BiFunction<CommandContext, AiDecisionDTO, AiCommandResponseDTO>> commandHandlers;

    public AiCommandServiceImpl(
            OpenAiClient openAiClient,
            ApplicationService applicationService,
            UserService userService,
            AiPromptRequestValidator requestValidator,
            AiDeterministicCommandMatcher deterministicMatcher,
            AppNameNormalizer appNameNormalizer
    ) {
        this.openAiClient = openAiClient;
        this.applicationService = applicationService;
        this.userService = userService;
        this.requestValidator = requestValidator;
        this.deterministicMatcher = deterministicMatcher;
        this.appNameNormalizer = appNameNormalizer;
        this.commandHandlers = Map.of(
                AiDeterministicCommandMatcher.COMMAND_LIST_APPS, this::handleListApps,
                AiDeterministicCommandMatcher.COMMAND_OPEN_APP, this::handleOpenApp,
                AiDeterministicCommandMatcher.COMMAND_SET_THEME, this::handleSetTheme
        );
    }

    @Override
    public AiCommandResponseDTO handlePrompt(String userId, String prompt, boolean enforceRateLimit) {
        AiPromptRequestValidator.ValidationResult validation = requestValidator.validate(userId, prompt, enforceRateLimit);
        if (validation.hasError()) {
            return validation.error();
        }

        UUID userUuid = validation.userId();
        String trimmedPrompt = prompt.trim();
        List<ApplicationResponseDTO> apps = applicationService.listByUserId(userUuid);
        CommandContext context = new CommandContext(userUuid, apps);

        AiDecisionDTO decision = deterministicMatcher.match(trimmedPrompt, apps)
                .orElseGet(() -> mapPromptWithLlm(trimmedPrompt));

        String command = normalizeCommand(decision.getCommand());
        BiFunction<CommandContext, AiDecisionDTO, AiCommandResponseDTO> handler = commandHandlers.getOrDefault(command, this::handleUnknown);
        return handler.apply(context, decision);
    }

    private AiDecisionDTO mapPromptWithLlm(String prompt) {
        try {
            return openAiClient.mapPromptToCommand(prompt);
        } catch (IllegalStateException ex) {
            return AiDecisionDTO.unknown(
                    "AI szolgaltatas jelenleg nem elerheto.",
                    "Ellenorizd az OPENAI_API_KEY beallitast, vagy probald ujra kesobb."
            );
        }
    }

    private AiCommandResponseDTO handleListApps(CommandContext context, AiDecisionDTO ignoredDecision) {
        List<ApplicationResponseDTO> apps = context.apps();
        if (apps.isEmpty()) {
            return AiCommandResponseDTO.success(AiDeterministicCommandMatcher.COMMAND_LIST_APPS, "Nincsenek alkalmazasok a felhasznalohoz.");
        }

        String names = apps.stream().map(ApplicationResponseDTO::getName).sorted().reduce((a, b) -> a + ", " + b).orElse("");
        return AiCommandResponseDTO.success(AiDeterministicCommandMatcher.COMMAND_LIST_APPS, "Alkalmazasok: " + names);
    }

    private AiCommandResponseDTO handleOpenApp(CommandContext context, AiDecisionDTO decision) {
        String appName = appNameArgument(decision);
        if (appName == null || appName.isBlank()) {
            return AiCommandResponseDTO.clarificationNeeded(
                    "Nem derult ki, melyik alkalmazast szeretned inditani.",
                    "Melyik alkalmazast inditsam el?"
            );
        }

        return handleOpenAppByName(context.apps(), appName);
    }

    private AiCommandResponseDTO handleOpenAppByName(List<ApplicationResponseDTO> apps, String appName) {
        String resolvedAppName = appNameNormalizer.normalizeInput(appName);
        if (resolvedAppName.isBlank()) {
            return AiCommandResponseDTO.clarificationNeeded(
                    "Nem derult ki, melyik alkalmazast szeretned inditani.",
                    "Melyik alkalmazast inditsam el?"
            );
        }

        boolean exists = appExistsForUser(apps, resolvedAppName);

        if (!exists) {
            return new AiCommandResponseDTO(
                    AiDeterministicCommandMatcher.COMMAND_OPEN_APP,
                    "Nem talaltam ilyen alkalmazast: " + resolvedAppName,
                    true,
                    "Mondd meg pontosan az alkalmazas nevet."
            );
        }

        return AiCommandResponseDTO.success(AiDeterministicCommandMatcher.COMMAND_OPEN_APP, "Alkalmazas elinditva: " + resolvedAppName);
    }

    private boolean appExistsForUser(List<ApplicationResponseDTO> apps, String appName) {
        String normalizedInput = appNameNormalizer.normalizeComparable(appName);
        if (normalizedInput.isBlank()) {
            return false;
        }

        return apps.stream()
                .map(ApplicationResponseDTO::getName)
                .map(appNameNormalizer::normalizeComparable)
                .anyMatch(existing -> existing.equals(normalizedInput));
    }

    private AiCommandResponseDTO handleSetTheme(CommandContext context, AiDecisionDTO decision) {
        String themeArg = themeArgument(decision);
        if (themeArg == null || themeArg.isBlank()) {
            return AiCommandResponseDTO.clarificationNeeded(
                    "Nem derult ki, melyik temat szeretned.",
                    "Vilagos vagy sotet temat allitsak be?"
            );
        }

        Theme theme;
        try {
            theme = Theme.valueOf(themeArg.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return AiCommandResponseDTO.clarificationNeeded(
                    "Ismeretlen tema: " + themeArg,
                    "A tema csak LIGHT vagy DARK lehet."
            );
        }

        userService.updateTheme(context.userId(), theme);
        return AiCommandResponseDTO.success(AiDeterministicCommandMatcher.COMMAND_SET_THEME, "Tema frissitve erre: " + theme.name());
    }

    private AiCommandResponseDTO handleUnknown(CommandContext ignoredContext, AiDecisionDTO decision) {
        String message = decision.getMessage();
        if (message == null || message.isBlank()) {
            message = "Nem sikerult biztosan felismerni a szandekot.";
        }

        String clarification = decision.getClarificationQuestion();
        if (clarification == null || clarification.isBlank()) {
            clarification = "Pontositsd kerlek, mit szeretnel csinalni.";
        }

        return AiCommandResponseDTO.clarificationNeeded(message, clarification);
    }

    private static String normalizeCommand(String command) {
        if (command == null) {
            return AiDeterministicCommandMatcher.COMMAND_UNKNOWN;
        }

        String normalized = command.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case AiDeterministicCommandMatcher.COMMAND_OPEN_APP,
                 AiDeterministicCommandMatcher.COMMAND_LIST_APPS,
                 AiDeterministicCommandMatcher.COMMAND_SET_THEME,
                 AiDeterministicCommandMatcher.COMMAND_UNKNOWN -> normalized;
            default -> AiDeterministicCommandMatcher.COMMAND_UNKNOWN;
        };
    }

    private static String appNameArgument(AiDecisionDTO decision) {
        String primary = valueFromArguments(decision, "appName");
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return valueFromArguments(decision, "app");
    }

    private static String themeArgument(AiDecisionDTO decision) {
        return valueFromArguments(decision, "theme");
    }

    private static String valueFromArguments(AiDecisionDTO decision, String key) {
        if (decision.getArguments() == null || decision.getArguments().isEmpty()) {
            return null;
        }

        String value = decision.getArguments().get(key);
        return value == null || value.isBlank() ? null : value;
    }

    private record CommandContext(UUID userId, List<ApplicationResponseDTO> apps) {}
}
