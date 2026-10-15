package com.maurodelcore.expensetracker;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests that /admin/users is open to an ADMIN token and closed to a USER token.
 */
class AdminIntegrationTest extends AbstractIntegrationTest {

    /** An admin gets 200 and a list that includes the admin and never a password. */
    @Test
    void adminCanListUsers() throws Exception {
        String adminToken = logIn("admin", ADMIN_PASSWORD);

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == 'admin')]").exists())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    /** A normal user gets 403. */
    @Test
    void userCannotListUsers() throws Exception {
        String userToken = signUp(uniqueUsername(), USER_PASSWORD);

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isForbidden());
    }
}