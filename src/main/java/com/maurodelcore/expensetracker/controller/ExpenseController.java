package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.service.ExpenseService;
import com.maurodelcore.expensetracker.utils.ExpenseDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/")
    public String start() {
        return "Expenses Tracker!";
    }

    @GetMapping("/expenses/categories")
    public ResponseEntity<List<String>> getAllExpensesCategory() {
        List<String> expenseCategory = expenseService.getAllExpensesCategories();
        return ResponseEntity.ok(expenseCategory);
    }

    @GetMapping("/expenses/categories/{day}")
    public ResponseEntity<List<Expense>> getExpensesByDay(@PathVariable String day) {
        return ResponseEntity.ok(expenseService.getExpensesByDay(day));
    }

    @GetMapping("/expenses/categories/{category}/month")
    public ResponseEntity<List<Expense>> getExpenseByCategoryAndMonth(@PathVariable String category, @RequestParam String month) {
        return ResponseEntity.ok(expenseService.getExpensesByCategoryAndMonth(category, month));
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Optional<Expense>> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(expense));
    }

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

