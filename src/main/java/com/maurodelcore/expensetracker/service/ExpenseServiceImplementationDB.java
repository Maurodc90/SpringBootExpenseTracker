package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.repository.ExpenseRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Database-backed implementation of {@link ExpenseService}, active only under the "db" profile.
 * Delegates all persistence to {@link ExpenseRepository}, replacing the in-memory/JSON-backed
 * implementation used under other profiles.
 */
@Service
@Profile("db")
public class ExpenseServiceImplementationDB implements ExpenseService{

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImplementationDB(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByDay(String date) {
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getDate().equals(date))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String category, String month) {
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category) &&
                        expense.getDate().startsWith(month))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllExpensesCategories() {
        return expenseRepository.findAll().stream()
                .map(Expense::getCategory)
                .distinct()
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteExpenseById(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateExpense(Expense updatedExpense) {
        if(expenseRepository.existsById(updatedExpense.getId())) {
            expenseRepository.save(updatedExpense);
            return true;
        }
        return false;
    }
}