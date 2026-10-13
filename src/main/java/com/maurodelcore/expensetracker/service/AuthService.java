package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.dto.AppUserDTO;
import com.maurodelcore.expensetracker.dto.AuthDTO;
import com.maurodelcore.expensetracker.dto.AuthResponseDTO;

/**
 * Authentication logic for the Expense Tracker: creating accounts and logging in.
 * Failures are reported by throwing exceptions, not by returning an error response;
 * the web layer turns them into HTTP status codes.
 */
public interface AuthService {

    /**
     * Creates a new user with a hashed password and logs them in.
     *
     * @param appUserDTO the full name, username and plain-text password of the new user
     * @return a response holding the token
     * @throws com.maurodelcore.expensetracker.exception.UsernameAlreadyExistsException if the username is already taken
     */
    AuthResponseDTO registerUser(AppUserDTO appUserDTO);

    /**
     * Checks the credentials of an existing user and generates a token.
     *
     * @param authDTO the username and plain-text password
     * @return a response holding the token
     * @throws org.springframework.security.authentication.BadCredentialsException if the username or password is wrong
     */
    AuthResponseDTO loginUser(AuthDTO authDTO);
}