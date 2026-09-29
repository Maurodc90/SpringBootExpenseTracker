package com.maurodelcore.expensetracker.repository;

import com.maurodelcore.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for {@link Expense} entities.
 * Provides CRUD operations via {@link JpaRepository} with no custom query methods yet.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}