package com.maurodelcore.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests sign-up and login: the happy paths, a duplicate username and a wrong password.
 */
class AuthIntegrationTest extends AbstractIntegrationTest {

    /** Signing up with a new username returns 201 and a token. */
    @Test
    void signupReturns201WithToken() throws Exception {
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(uniqueUsername(), USER_PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /** Signing up twice with the same username returns 409 the second time. */
    @Test
    void duplicateSignupReturns409() throws Exception {
        String username = uniqueUsername();
        signUp(username, USER_PASSWORD);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson(username, USER_PASSWORD)))
                .andExpect(status().isConflict());
    }

    /** Logging in with the right password returns 200 and a token. */
    @Test
    void loginWithCorrectPasswordReturns200WithToken() throws Exception {
        String username = uniqueUsername();
        signUp(username, USER_PASSWORD);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(username, USER_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /** Logging in with the wrong password returns 401. */
    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String username = uniqueUsername();
        signUp(username, USER_PASSWORD);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(username, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }
}