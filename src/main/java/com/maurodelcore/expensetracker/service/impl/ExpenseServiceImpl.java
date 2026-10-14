package com.maurodelcore.expensetracker.service.impl;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.repository.ExpenseRepository;
import com.maurodelcore.expensetracker.service.ExpenseService;
import com.maurodelcore.expensetracker.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database-backed implementation of {@link ExpenseService}, using {@link ExpenseRepository}.
 * Every operation is limited to the expenses of the given user, so one user can
 * never read, change or delete another user's expenses.
 */
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    /**
     * Creates the service with the repository and the user service it depends on.
     *
     * @param expenseRepository the repository used to read and write expenses
     * @param userService       the service used to load the owner of a new expense
     */
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getAllUserExpenses(Long userId) {
        return new ArrayList<>(expenseRepository.findAllByUserIdOrderByDateDesc(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByDay(String date, Long userID) {
        return expenseRepository.findAllByUserIdOrderByDateDesc(userID).stream()
                .filter(expense -> expense.getDate().equals(date))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String category, String month, Long userId) {
        return expenseRepository.findAllByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category) &&
                        expense.getDate().startsWith(month))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllExpensesCategories(Long userId) {
        return expenseRepository.findAllByUserIdOrderByDateDesc(userId)
                .stream()
                .map(Expense::getCategory)
                .distinct()
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Expense> getExpenseById(Long id, Long userId) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException if no user exists with the given id
     */
    @Override
    public Expense addExpense(Expense expense, Long userId) {
        // Look up the owner; the result is an Optional because the user may not exist
        Optional<AppUser> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            expense.setUser(user);
            return expenseRepository.save(expense);
        } else {
            throw new RuntimeException("User not found");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateExpense(Expense updatedExpense, Long userId) {
        // Find the expense by id and user id, so a user can only update their own expenses
        Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(updatedExpense.getId(), userId);
        if (existingExpense.isPresent()) {
            updatedExpense.setUser(existingExpense.get().getUser());
            expenseRepository.save(updatedExpense);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteExpenseById(Long id, Long userId) {
        Optional<Expense> expenseToDelete = expenseRepository.findByIdAndUserId(id, userId);
        if (expenseToDelete.isPresent()) {
            expenseRepository.deleteById(expenseToDelete.get().getId());
            return true;
        }
        return false;
    }
}