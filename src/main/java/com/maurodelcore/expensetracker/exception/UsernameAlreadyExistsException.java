package com.maurodelcore.expensetracker.exception;

/**
 * Thrown when someone tries to sign up with a username that already exists.
 * It is unchecked, so callers do not need to declare it, and
 * {@link GlobalExceptionHandler} turns it into a 409 Conflict response.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * Creates the exception with a fixed message, because this error always means the same thing.
     */
    public UsernameAlreadyExistsException() {
        super("Username already exists, please use another username");
    }

}