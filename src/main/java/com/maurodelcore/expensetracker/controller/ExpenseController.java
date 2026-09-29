package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing CRUD and filtering endpoints for {@link Expense} records.
 * Delegates all business logic to {@link ExpenseService}.
 */
@RestController
public class ExpenseController {


    final private ExpenseService expenseService;


    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
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
     * Returns the distinct set of expense categories currently in use.
     *
     * @return 200 OK with the list of category names, empty if there are none
     */
    @GetMapping("/expenses/categories")
    public ResponseEntity<List<String>> getAllExpensesCategory() {
        List<String> expenseCategory = expenseService.getAllExpensesCategories();
        return ResponseEntity.ok(expenseCategory);
    }

    /**
     * Returns all expenses recorded on a given day.
     *
     * @param day the date to filter by
     * @return 200 OK with matching expenses, empty if none match
     */
    @GetMapping("/expenses/categories/{day}")
    public ResponseEntity<List<Expense>> getExpensesByDay(@PathVariable String day) {
        return ResponseEntity.ok(expenseService.getExpensesByDay(day));
    }

    /**
     * Returns expenses matching a given category within a given month.
     *
     * @param category the category to filter by
     * @param month the month to filter by
     * @return 200 OK with matching expenses, empty if none match
     */
    @GetMapping("/expenses/categories/{category}/month")
    public ResponseEntity<List<Expense>> getExpenseByCategoryAndMonth(@PathVariable String category, @RequestParam String month) {
        return ResponseEntity.ok(expenseService.getExpensesByCategoryAndMonth(category, month));
    }

    /**
     * Looks up a single expense by id.
     *
     * @param id the id of the expense to find
     * @return 200 OK with the expense if found, otherwise 404 Not Found
     */
    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        if (expense.isPresent()) {
            return ResponseEntity.ok(expense.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new expense.
     *
     * @param expense the expense to create, supplied in the request body
     * @return 201 Created with the saved expense
     */
    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(expense));
    }

    /**
     * Updates an existing expense.
     *
     * @param id the id of the expense to update, taken from the path
     * @param expense the updated expense data, supplied in the request body
     * @return 200 OK with the updated expense if it existed, otherwise 404 Not Found
     */
    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        expense.setId(id);
        boolean isUpdated = expenseService.updateExpense(expense);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body(expense);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Deletes an expense by id.
     *
     * @param id the id of the expense to delete
     * @return 204 No Content if the expense was deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        boolean isDeleted = expenseService.deleteExpenseById(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}