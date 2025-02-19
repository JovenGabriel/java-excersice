package com.threeit.users.utils;

import com.threeit.users.entities.User;
import com.threeit.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Checks the existence of an admin user by email and creates a new admin user
     * with predefined credentials and properties if not present in the system storage.
     *
     * @param args command line arguments passed to the execution context; not used in this implementation
     */
    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .isActive(true)
                    .build();
            userRepository.save(admin);
        }
    }
}