package com.maurodelcore.expensetracker.config;

import com.maurodelcore.expensetracker.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Expense Tracker.
 *
 * <p>Requests are authenticated with a JWT sent in the {@code Authorization} header.
 * No HTTP session is created, so every request must carry its own token. Only the
 * sign-up and login endpoints (and Spring's internal {@code /error} page) are public.
 * The {@code /admin/**} endpoints require the ADMIN role; every other endpoint requires
 * the USER role.
 *
 * <p>A request with no valid token receives 401 Unauthorized. A request from a known
 * user with the wrong role receives 403 Forbidden.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Creates the configuration with the filter that reads the JWT on each request.
     *
     * @param jwtAuthFilter the filter that validates the token and identifies the user
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Exposes the {@link AuthenticationManager} so the login service can check
     * a username and password against the database users.
     *
     * @param authenticationConfiguration Spring's configuration that builds the manager
     * @return the authentication manager used at login
     * @throws Exception if the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides the BCrypt encoder. The same bean hashes passwords at sign-up and
     * checks them at login, so the hashes always match when the password is right.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines which requests are public, which need a token, which role each
     * group of endpoints requires, how a missing token is answered, and where
     * the JWT filter runs.
     *
     * @param http the builder Spring provides for the security rules
     * @return the configured security filter chain
     * @throws Exception if the chain cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection is not needed: the token travels in a header the browser does not attach automatically
                .csrf(csrf -> csrf.disable())
                // No session is created or used; the token identifies the user on every request
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public before the user has a token (the first matching rule wins).
                        // /error must be public too: Spring forwards errors to /error internally,
                        // and that forward is checked by these rules again; otherwise a 403 would turn into a 401.
                        .requestMatchers("/signup/**", "/login/**", "/error/**").permitAll()
                        // Only users with the ADMIN role can access the admin endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // All other endpoints require the USER role, so admins cannot manage expenses
                        .anyRequest().hasRole("USER")
                )
                // A request with no valid token gets a plain 401 instead of Spring's default response
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // Read the token before Spring's own login filter runs
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}