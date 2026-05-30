package edu.example.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.example.core.entity.User;
import edu.example.core.entity.UserRole;
import edu.example.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Creating seed users...");

            User admin = new User(
                "Admin",
                "admin@demo.ya",
                30,
                passwordEncoder.encode("admin123"),
                List.of(UserRole.ADMIN, UserRole.USER)
            );
            userRepository.save(admin);

            User user = new User(
                "User",
                "user@demo.ya",
                25,
                passwordEncoder.encode("user123"),
                List.of(UserRole.USER)
            );
            userRepository.save(user);

            log.info("Seed users created: admin@demo.ya / admin123, user@demo.ya / user123");
        }
    }
}
