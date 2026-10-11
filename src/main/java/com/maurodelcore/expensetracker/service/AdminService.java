package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.AppUser;

import java.util.List;

/**
 * Service contract for operations only an administrator may perform.
 */
public interface AdminService {

    /**
     * Returns all registered users.
     *
     * @return the list of all users, empty if none are registered
     */
    List<AppUser> getAllUsers();
}