package com.maurodelcore.expensetracker;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for the integration tests. It starts one real PostgreSQL in a throwaway
 * Docker container, shared by all test classes, and points the application at it, so
 * the tests never touch the Neon databases. The full application runs, including the
 * security filter chain, and requests go through MockMvc.
 */
@SpringBootTest(properties = "admin.password=" + AbstractIntegrationTest.ADMIN_PASSWORD)
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

    /** Password of the admin account created at startup, set only for the tests. */
    protected static final String ADMIN_PASSWORD = "test-admin-password";

    /** Password used for every test user. */
    protected static final String USER_PASSWORD = "test-password";

    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    /**
     * Replaces the datasource settings from application.yml with the container's.
     *
     * @param registry the registry Spring uses to add test properties
     */
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    /**
     * Creates a username no other test uses, so tests never collide on the shared database.
     *
     * @return a new unique username
     */
    protected static String uniqueUsername() {
        return "user" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * Builds the Authorization header value for a token.
     *
     * @param token the JWT
     * @return the header value, {@code Bearer <token>}
     */
    protected static String bearer(String token) {
        return "Bearer " + token;
    }

    /**
     * Builds a sign-up request body.
     *
     * @param username the username
     * @param password the password
     * @return the JSON body
     */
    protected static String signupJson(String username, String password) {
        return """
                {"fullName": "Test User", "username": "%s", "password": "%s"}
                """.formatted(username, password);
    }

    /**
     * Builds a login request body.
     *
     * @param username the username
     * @param password the password
     * @return the JSON body
     */
    protected static String loginJson(String username, String password) {
        return """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);
    }

    /**
     * Builds an expense request body without an id.
     *
     * @param amount the amount
     * @return the JSON body
     */
    protected static String expenseJson(double amount) {
        return """
                {"account": "Cash", "amount": %s, "category": "Food",
                 "date": "2026-10-15", "expenseType": 0, "note": "Groceries"}
                """.formatted(amount);
    }

    /**
     * Builds an expense request body that also carries an id, as a malicious client would.
     *
     * @param id     the id the client tries to force
     * @param amount the amount
     * @return the JSON body
     */
    protected static String expenseJsonWithId(long id, double amount) {
        return """
                {"id": %s, "account": "Cash", "amount": %s, "category": "Food",
                 "date": "2026-10-15", "expenseType": 0, "note": "Groceries"}
                """.formatted(id, amount);
    }

    /**
     * Signs a new user up and returns the token from the response.
     *
     * @param username the username to register
     * @param password the password to register
     * @return the JWT returned by /signup
     * @throws Exception if the request fails
     */
    protected String signUp(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(username, password)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    /**
     * Logs an existing user in and returns the token from the response.
     *
     * @param username the username
     * @param password the password
     * @return the JWT returned by /login
     * @throws Exception if the request fails
     */
    protected String logIn(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(username, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    /**
     * Creates an expense for the user behind the token and returns its id.
     *
     * @param token  the JWT of the owner
     * @param amount the amount of the expense
     * @return the id assigned by the database
     * @throws Exception if the request fails
     */
    protected long createExpense(String token, double amount) throws Exception {
        String body = mockMvc.perform(post("/expenses")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJson(amount)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}