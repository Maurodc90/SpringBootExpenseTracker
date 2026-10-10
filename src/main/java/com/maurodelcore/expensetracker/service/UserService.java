package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.AppUser;

/**
 * Stores and looks up application users.
 */
public interface UserService {

    /**
     * Saves a new user to the database.
     *
     * @param appUser the user to save, with the password already hashed
     * @return the saved user, including the generated id
     */
    AppUser saveUser(AppUser appUser);

    /**
     * Finds a user by their login name.
     *
     * @param username the login name to look up
     * @return the matching user, or {@code null} if no user has that username
     */
    AppUser findByUsername(String username);
}