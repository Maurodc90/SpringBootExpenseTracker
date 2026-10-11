package com.maurodelcore.expensetracker.repository;

import com.maurodelcore.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link Expense} entities.
 * Provides CRUD operations via {@link JpaRepository}, plus derived queries that
 * restrict results to the expenses of a single user.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Finds all expenses of a user, newest first.
     *
     * @param userId the id of the user who owns the expenses
     * @return the user's expenses ordered by date descending, empty if there are none
     */
    List<Expense> findAllByUserIdOrderByDateDesc(Long userId);

    /**
     * Finds one expense by id, only if it belongs to the given user.
     *
     * @param id     the id of the expense
     * @param UserId the id of the user who must own the expense
     * @return the expense if it exists and belongs to that user, otherwise empty
     */
    Optional<Expense> findByIdAndUserId(Long id, Long UserId);
}