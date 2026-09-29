package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.utils.ExpenseDataLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * In-memory, JSON-backed implementation of {@link ExpenseService}, active only under the "json" profile.
 * Reads and mutates the shared expense list held by {@link ExpenseDataLoader}.
 */
@Service
@Profile("json")
public class ExpenseServiceImplementation implements ExpenseService{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByDay(String date) {
        return ExpenseDataLoader.getExpenses().stream()
                .filter(expense -> expense.getDate().equals(date))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String category, String month) {
        return ExpenseDataLoader.getExpenses().stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category) &&
                        expense.getDate().startsWith(month))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllExpensesCategories() {
        return ExpenseDataLoader
                .getExpenses()
                .stream()
                .map(Expense::getCategory)
                .distinct()
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Expense> getExpenseById(Long id) {
        return ExpenseDataLoader.getExpenses()
                .stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense addExpense(Expense expense) {
        expense.setId(ExpenseDataLoader.getNextId());
        ExpenseDataLoader.getExpenses().add(expense);
        return expense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteExpenseById(Long id) {
        Optional<Expense> expenseToDelete = getExpenseById(id);
        if (expenseToDelete.isPresent()) {
            ExpenseDataLoader.getExpenses().remove(expenseToDelete.get());
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateExpense(Expense updatedExpense) {
        Optional<Expense> existingExpense = getExpenseById(updatedExpense.getId());
        if (existingExpense.isPresent()) {
            ExpenseDataLoader.getExpenses().remove(existingExpense.get());
            ExpenseDataLoader.getExpenses().add(updatedExpense);
            return true;
        }
        return false;
    }
}