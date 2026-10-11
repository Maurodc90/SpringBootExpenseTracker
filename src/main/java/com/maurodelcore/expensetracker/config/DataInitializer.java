package com.maurodelcore.expensetracker.config;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.model.Role;
import com.maurodelcore.expensetracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with a default administrator account at application startup.
 * For simplicity, this is the only way an {@link Role#ADMIN} user is created;
 * every other account registered through the API is given the {@link Role#USER} role.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the initializer with the dependencies it needs to look up and save users.
     *
     * @param userRepository  repository used to check for and save the admin user
     * @param passwordEncoder encoder used to hash the admin password before it is stored
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs once after the application context has started. Creates the admin user
     * if no user named "admin" exists yet, so restarting the application never
     * creates a duplicate.
     *
     * @param args command-line arguments passed to the application (not used)
     */
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser adminUser = new AppUser();
            adminUser.setFullName("Admin");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);
            System.out.println("Admin user created. Username: admin, Pass: admin");

        }

    }
}