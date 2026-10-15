package com.maurodelcore.expensetracker;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the expense endpoints: authentication, ownership, and the ignored client id.
 */
class ExpenseIntegrationTest extends AbstractIntegrationTest {

    /** A request without a token returns 401. */
    @Test
    void requestWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isUnauthorized());
    }

    /** Adding an expense returns 201 and the database assigns the id. */
    @Test
    void addExpenseReturns201WithGeneratedId() throws Exception {
        String token = signUp(uniqueUsername(), USER_PASSWORD);

        mockMvc.perform(post("/expenses")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJson(42.5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(42.5));
    }

    /** A user sees exactly their own expenses in the list. */
    @Test
    void getOwnExpensesReturns200WithTheCreatedExpense() throws Exception {
        String token = signUp(uniqueUsername(), USER_PASSWORD);
        long id = createExpense(token, 10.0);

        mockMvc.perform(get("/expenses")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
    }

    /** Asking for another user's expense by id returns 404, not the expense. */
    @Test
    void getOtherUsersExpenseReturns404() throws Exception {
        String ownerToken = signUp(uniqueUsername(), USER_PASSWORD);
        long ownerExpenseId = createExpense(ownerToken, 10.0);
        String otherToken = signUp(uniqueUsername(), USER_PASSWORD);

        mockMvc.perform(get("/expenses/" + ownerExpenseId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
    }

    /**
     * An id sent in the body of a create request is ignored: a new row is made and
     * the existing expense stays unchanged.
     */
    @Test
    void clientSuppliedIdIsIgnoredOnCreate() throws Exception {
        String token = signUp(uniqueUsername(), USER_PASSWORD);
        long originalId = createExpense(token, 42.5);

        String body = mockMvc.perform(post("/expenses")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseJsonWithId(originalId, 999.99)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long newId = ((Number) JsonPath.read(body, "$.id")).longValue();

        assertThat(newId).isNotEqualTo(originalId);
        mockMvc.perform(get("/expenses/" + originalId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(42.5));
    }

    /** An admin token cannot use the expense endpoints. */
    @Test
    void adminOnExpenseEndpointReturns403() throws Exception {
        String adminToken = logIn("admin", ADMIN_PASSWORD);

        mockMvc.perform(get("/expenses/categories")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isForbidden());
    }
}