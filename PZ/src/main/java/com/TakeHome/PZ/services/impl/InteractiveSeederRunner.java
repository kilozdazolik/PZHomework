package com.TakeHome.PZ.services.impl;

import com.TakeHome.PZ.models.Application;
import com.TakeHome.PZ.models.Enums.Role;
import com.TakeHome.PZ.models.Enums.Theme;
import com.TakeHome.PZ.models.Family;
import com.TakeHome.PZ.models.User;
import com.TakeHome.PZ.repository.ApplicationRepository;
import com.TakeHome.PZ.repository.FamilyRepository;
import com.TakeHome.PZ.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class InteractiveSeederRunner implements CommandLineRunner {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s'-]+$");
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public InteractiveSeederRunner(
            FamilyRepository familyRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository
    ) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Interactive Seeder ===");
        System.out.print("Do you want to add a user with favorite apps? (y/n): ");
        String shouldSeed = scanner.nextLine().trim();

        if (!"y".equalsIgnoreCase(shouldSeed) && !"yes".equalsIgnoreCase(shouldSeed)) {
            System.out.println("Seeder skipped.");
            return;
        }

        String familyName = promptValidName(scanner, "Family name");
        String userName = promptValidName(scanner, "User name");

        System.out.print("Favorite apps (comma separated, e.g. Spotify, VS Code): ");
        String appsInput = scanner.nextLine().trim();

        Set<String> favoriteApps = Arrays.stream(appsInput.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (favoriteApps.isEmpty()) {
            System.out.println("At least one favorite app is required. Seeder cancelled.");
            return;
        }

        Family family = familyRepository.findByNameIgnoreCase(familyName)
                .orElseGet(() -> familyRepository.save(Family.builder().name(familyName).build()));

        User user = userRepository.findByNameIgnoreCase(userName)
                .orElseGet(() -> {
                    Role role = userRepository.countByFamilyId(family.getId()) == 0 ? Role.ADMIN : Role.USER;
                    User createdUser = User.builder()
                            .name(userName)
                            .role(role)
                            .theme(Theme.LIGHT)
                            .family(family)
                            .build();
                    return userRepository.save(createdUser);
                });

        if (user.getFamily() == null || !family.getId().equals(user.getFamily().getId())) {
            user.setFamily(family);
            user = userRepository.save(user);
        }

        List<String> existingApps = applicationRepository.findByUserId(user.getId()).stream()
                .map(Application::getName)
                .toList();

        int addedCount = 0;
        for (String appName : favoriteApps) {
            boolean exists = existingApps.stream().anyMatch(existing -> existing.equalsIgnoreCase(appName));
            if (!exists) {
                applicationRepository.save(new Application(null, appName, user));
                addedCount++;
            }
        }

        System.out.println("Seeder completed successfully.");
        System.out.println("Family: " + family.getName());
        System.out.println("User: " + user.getName());
        System.out.println("Added apps: " + addedCount);
    }

    private static String promptValidName(Scanner scanner, String fieldLabel) {
        while (true) {
            System.out.print(fieldLabel + ": ");
            String input = scanner.nextLine().trim();

            if (input.length() < MIN_NAME_LENGTH || input.length() > MAX_NAME_LENGTH) {
                System.out.printf("%s must be between %d and %d characters.%n", fieldLabel, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
                continue;
            }

            if (!NAME_PATTERN.matcher(input).matches()) {
                System.out.println(fieldLabel + " can only contain letters, spaces, apostrophes, and hyphens.");
                continue;
            }

            return input;
        }
    }
}
