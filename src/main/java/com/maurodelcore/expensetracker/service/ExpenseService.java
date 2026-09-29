package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;

import java.util.List;
import java.util.Optional;

/**
 * Defines the operations available on expense data, independent of how it is persisted.
 * Implemented separately per profile (e.g. database-backed vs JSON-backed).
 */
public interface ExpenseService {

    /**
     * Returns all expenses recorded on a given date.
     *
     * @param date the date to filter by
     * @return matching expenses, or an empty list if none match
     */
    List<Expense> getExpensesByDay(String date);

    /**
     * Returns all expenses matching a given category within a given month.
     *
     * @param category the category to filter by
     * @param month the month to filter by
     * @return matching expenses, or an empty list if none match
     */
    List<Expense> getExpensesByCategoryAndMonth(String category, String month);

    /**
     * Returns the distinct set of categories present across all expenses.
     *
     * @return a list of unique category names
     */
    List<String> getAllExpensesCategories();

    /**
     * Looks up a single expense by its id.
     *
     * @param id the id of the expense to find
     * @return the expense if found, otherwise empty
     */
    Optional<Expense> getExpenseById(Long id);

    /**
     * Persists a new expense.
     *
     * @param expense the expense to save
     * @return the saved expense, including any id assigned by the backing store
     */
    Expense addExpense(Expense expense);

    /**
     * Deletes an expense by id, if it exists.
     *
     * @param id the id of the expense to delete
     * @return true if an expense with that id existed and was deleted, false otherwise
     */
    boolean deleteExpenseById(Long id);

    /**
     * Updates an existing expense, if it exists.
     *
     * @param expense the expense with updated fields; its id determines which record is updated
     * @return true if an expense with that id existed and was updated, false otherwise
     */
    boolean updateExpense(Expense expense);
}