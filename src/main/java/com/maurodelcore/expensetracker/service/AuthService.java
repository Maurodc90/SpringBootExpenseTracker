package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.dto.AppUserDTO;
import com.maurodelcore.expensetracker.dto.AuthDTO;
import com.maurodelcore.expensetracker.dto.AuthResponseDTO;

/**
 * Authentication logic for the Expense Tracker: creating accounts and logging in.
 */
public interface AuthService {

    /**
     * Creates a new user with a hashed password and logs them in.
     *
     * @param appUserDTO the full name, username and plain-text password of the new user
     * @return a response with the token and the message "success", or without a token
     *         and with an error message if the username is already taken
     */
    AuthResponseDTO registerUser(AppUserDTO appUserDTO);

    /**
     * Checks the credentials of an existing user and generates a token.
     *
     * @param authDTO the username and plain-text password
     * @return a response with the token and the message "success", or without a token
     *         and with an error message if the credentials are invalid
     */
    AuthResponseDTO loginUser(AuthDTO authDTO);
}