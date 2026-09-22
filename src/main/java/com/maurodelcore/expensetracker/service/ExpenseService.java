package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    List<Expense> getExpensesByDay(String date);
    List<Expense> getExpensesByCategoryAndMonth(String category, String month);
    List<String> getAllExpensesCategories();
    Optional<Expense> getExpenseById(Long id);
    Expense addExpense(Expense expense);
    boolean deleteExpenseById(Long id);
    boolean updateExpense(Expense expense);
}
