package com.maurodelcore.expensetracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * JPA entity representing a single expense record.
 * {@code @Data} generates getters, setters, {@code equals}/{@code hashCode}, and {@code toString}.
 */
@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int expenseType;
    private String date;
    private Double amount;
    private String category;
    private String account;
    private String note;
}