package com.maurodelcore.expensetracker.model;

import lombok.Data;

@Data
public class Expense {
    private Long id;
    private int expenseType;
    private String date;
    private Double amount;
    private String category;
    private String account;
    private String note;
}
