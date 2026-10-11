package com.maurodelcore.expensetracker.model;

/**
 * The roles a user can have. The role decides which endpoints the user may call:
 * {@link #USER} manages their own expenses, {@link #ADMIN} uses the {@code /admin} endpoints.
 */
public enum Role {

    /**
     * A regular user who adds, edits and deletes their own expenses.
     */
    USER,

    /**
     * An administrator who manages users but not expenses.
     */
    ADMIN
}