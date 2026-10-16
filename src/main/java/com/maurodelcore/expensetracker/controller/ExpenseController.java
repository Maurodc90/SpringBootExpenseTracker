package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.service.ExpenseService;
import com.maurodelcore.expensetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing CRUD and filtering endpoints for {@link Expense} records.
 * Every endpoint works on the expenses of the logged-in user, identified from the
 * JWT. Delegates all business logic to {@link ExpenseService}.
 */
@RestController
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    /**
     * Creates the controller with the services it delegates to.
     *
     * @param expenseService the service holding the expense logic
     * @param userService    the service used to find the logged-in user
     */
    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    /**
     * Basic landing endpoint confirming the application is running.
     *
     * @return a static greeting string
     */
    @GetMapping("/")
    public String start() {
        return "Expenses Tracker!";
    }

    /**
     * Returns all expenses owned by the authenticated user.
     *
     * @param authentication the authenticated user resolved from the JWT
     * @return 200 OK with the user's expenses, or an empty list if there are none
     */
    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses(Authentication authentication) {
        AppUser user = userService.findByUsername(authentication.getName());
        List<Expense> expenses = expenseService.getAllUserExpenses(user.getId());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Returns the distinct set of expense categories used by the logged-in user.
     *
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 200 OK with the list of category names, empty if there are none
     */
    @GetMapping("/expenses/categories")
    public ResponseEntity<List<String>> getAllExpensesCategory(Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<String> expenseCategory = expenseService.getAllExpensesCategories(user.getId());
        return ResponseEntity.ok(expenseCategory);
    }

    /**
     * Returns all expenses of the logged-in user recorded on a given day.
     *
     * @param day            the date to filter by
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 200 OK with matching expenses, empty if none match
     */
    @GetMapping("/expenses/categories/{day}")
    public ResponseEntity<List<Expense>> getExpensesByDay(@PathVariable String day, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByDay(day, user.getId());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Returns expenses of the logged-in user matching a given category within a given month.
     *
     * @param category       the category to filter by
     * @param month          the month to filter by
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 200 OK with matching expenses, empty if none match
     */
    @GetMapping("/expenses/categories/{category}/month")
    public ResponseEntity<List<Expense>> getExpenseByCategoryAndMonth(@PathVariable String category, @RequestParam String month, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByCategoryAndMonth(category, month, user.getId());
        return ResponseEntity.ok(expenses);
    }

    /**
     * Looks up a single expense of the logged-in user by id.
     *
     * @param id             the id of the expense to find
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 200 OK with the expense if found, otherwise 404 Not Found
     */
    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        Optional<Expense> expense = expenseService.getExpenseById(id, user.getId());
        if (expense.isPresent()) {
            return ResponseEntity.ok(expense.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new expense owned by the logged-in user. Any id in the request body is
     * ignored, so a client can never overwrite an existing expense; the database assigns the id.
     *
     * @param expense        the expense to create, supplied in the request body
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 201 Created with the saved expense, including its new id, or 400 Bad Request if a field is invalid
     */
    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        // Never trust an id from the client: a null id makes save() create a new row
        expense.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(expense, user.getId()));
    }

    /**
     * Updates an existing expense of the logged-in user.
     *
     * @param id             the id of the expense to update, taken from the path
     * @param expense        the updated expense data, supplied in the request body
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 200 OK with the expense as sent in the request if it existed, 404 Not Found if it did not,
     *         or 400 Bad Request if a field is invalid
     */
    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        expense.setId(id);
        boolean isUpdated = expenseService.updateExpense(expense, user.getId());
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body(expense);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Deletes an expense of the logged-in user by id.
     *
     * @param id             the id of the expense to delete
     * @param authentication the authenticated user, supplied by Spring Security
     * @return 204 No Content if the expense was deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        boolean isDeleted = expenseService.deleteExpenseById(id, user.getId());
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}