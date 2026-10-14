package com.maurodelcore.expensetracker.config;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.model.Role;
import com.maurodelcore.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
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
    private final String adminPassword;

    /**
     * Creates the initializer with the dependencies and the admin password it needs.
     *
     * @param userRepository  repository used to check for and save the admin user
     * @param passwordEncoder encoder used to hash the admin password before it is stored
     * @param adminPassword   the admin password, read from the {@code admin.password}
     *                        property (environment variable {@code ADMIN_PASSWORD});
     *                        falls back to a local-development default if it is not set
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${admin.password:defaultadmin}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
    }

    /**
     * Runs once after the application context has started. Creates the admin user
     * if no user named "admin" exists yet, so restarting the application never
     * creates a duplicate. An existing admin is left unchanged, so a new configured
     * password does not affect it.
     *
     * @param args command-line arguments passed to the application (not used)
     * @throws Exception if saving the admin user fails
     */
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser adminUser = new AppUser();
            adminUser.setFullName("Admin");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);
            System.out.println("Admin user created. Username: " + adminUser.getUsername());
        }
    }
}