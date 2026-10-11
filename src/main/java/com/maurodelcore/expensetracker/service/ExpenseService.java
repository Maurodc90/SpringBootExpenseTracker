package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;

import java.util.List;
import java.util.Optional;

/**
 * Defines the operations available on expense data, independent of how it is persisted.
 * Every operation works only on the expenses of the user identified by {@code userId}.
 */
public interface ExpenseService {

    /**
     * Returns all expenses of a user.
     *
     * @param userId the id of the user who owns the expenses
     * @return the user's expenses, or an empty list if there are none
     */
    List<Expense> getAllUserExpenses(Long userId);

    /**
     * Returns all expenses of a user recorded on a given date.
     *
     * @param date   the date to filter by
     * @param userId the id of the user who owns the expenses
     * @return matching expenses, or an empty list if none match
     */
    List<Expense> getExpensesByDay(String date, Long userId);

    /**
     * Returns all expenses of a user matching a given category within a given month.
     *
     * @param category the category to filter by
     * @param month    the month to filter by
     * @param userId   the id of the user who owns the expenses
     * @return matching expenses, or an empty list if none match
     */
    List<Expense> getExpensesByCategoryAndMonth(String category, String month, Long userId);

    /**
     * Returns the distinct set of categories present across a user's expenses.
     *
     * @param userId the id of the user who owns the expenses
     * @return a list of unique category names
     */
    List<String> getAllExpensesCategories(Long userId);

    /**
     * Looks up a single expense of a user by its id.
     *
     * @param id     the id of the expense to find
     * @param userId the id of the user who must own the expense
     * @return the expense if it exists and belongs to that user, otherwise empty
     */
    Optional<Expense> getExpenseById(Long id, Long userId);

    /**
     * Persists a new expense owned by the given user.
     *
     * @param expense the expense to save
     * @param userId  the id of the user who will own the expense
     * @return the saved expense, including any id assigned by the backing store
     */
    Expense addExpense(Expense expense, Long userId);

    /**
     * Deletes an expense by id, if it exists and belongs to the given user.
     *
     * @param id     the id of the expense to delete
     * @param userId the id of the user who must own the expense
     * @return true if the expense existed, belonged to that user and was deleted, false otherwise
     */
    boolean deleteExpenseById(Long id, Long userId);

    /**
     * Updates an existing expense, if it exists and belongs to the given user.
     *
     * @param expense the expense with updated fields; its id determines which record is updated
     * @param userId  the id of the user who must own the expense
     * @return true if the expense existed, belonged to that user and was updated, false otherwise
     */
    boolean updateExpense(Expense expense, Long userId);
}