package com.maurodelcore.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests sign-up and login: the happy paths, a duplicate username, a wrong password
 * and invalid request bodies.
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

    /** Signing up with a blank username and password returns 400 naming both fields. */
    @Test
    void signupWithBlankUsernameAndPasswordReturns400() throws Exception {
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson("", " ")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("username must not be blank"))
                .andExpect(jsonPath("$.password").value("password must not be blank"))
                .andExpect(jsonPath("$.fullName").doesNotExist());
    }

    /** Signing up without a full name returns 400 naming that field. */
    @Test
    void signupWithoutFullNameReturns400() throws Exception {
        String bodyWithoutFullName = """
                {"username": "%s", "password": "%s"}
                """.formatted(uniqueUsername(), USER_PASSWORD);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithoutFullName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").value("fullName must not be blank"));
    }

    /** Logging in with a blank username returns 400 naming only that field. */
    @Test
    void loginWithBlankUsernameReturns400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("", USER_PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("username must not be blank"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}