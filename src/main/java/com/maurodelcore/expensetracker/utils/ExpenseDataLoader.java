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

@Component
public class ExpenseDataLoader {



    private static List<Expense> expenses = new ArrayList<>();

    // atomiclong ensure that at the moment of creation of a new expense, the id is unique even if 2 user will add an expense at the same time
    private static AtomicLong ID_COUNTER = new AtomicLong();

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = this.getClass().getResourceAsStream("/expenses.json");
        expenses = objectMapper.readValue(is, new TypeReference<List<Expense>>() {});
        ID_COUNTER.set(expenses.stream().mapToLong(Expense::getId).max().orElse(0L));

    }

    public static List<Expense> getExpenses() {
        return expenses;
    }

    public static long getNextId() {
        return  ID_COUNTER.incrementAndGet();
    }


}
