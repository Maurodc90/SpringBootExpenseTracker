package com.maurodelcore.expensetracker.utils;

import com.maurodelcore.expensetracker.model.Expense;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Loads expense data from {@code expenses.json} on startup and holds it in memory
 * for the "json" profile's {@link com.maurodelcore.expensetracker.service.ExpenseServiceImplementation}.
 * Also tracks the next available id via {@link #getNextId()}.
 */
@Component
public class ExpenseDataLoader {

    private static List<Expense> expenses = new ArrayList<>();

    // AtomicLong ensures the id stays unique even if two requests add an expense at the same time
    private static AtomicLong ID_COUNTER = new AtomicLong();

    /**
     * Reads {@code expenses.json} from the resources folder into {@link #expenses}, then
     * initializes {@link #ID_COUNTER} to the highest existing id (or 0 if the file is empty),
     * so newly added expenses continue from where the file left off rather than restarting at 1.
     */
    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = this.getClass().getResourceAsStream("/expenses.json");
        expenses = objectMapper.readValue(is, new TypeReference<List<Expense>>() {});
        ID_COUNTER.set(expenses.stream().mapToLong(Expense::getId).max().orElse(0L));
    }

    /**
     * Returns the shared in-memory list of expenses.
     *
     * @return the current list of expenses
     */
    public static List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Returns the next available expense id, atomically incrementing the internal counter.
     *
     * @return a unique id, higher than any previously issued
     */
    public static long getNextId() {
        return ID_COUNTER.incrementAndGet();
    }

}