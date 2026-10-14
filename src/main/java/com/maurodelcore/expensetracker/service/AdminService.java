package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.dto.UserListDTO;

import java.util.List;

/**
 * Service contract for operations only an administrator may perform.
 */
public interface AdminService {

    /**
     * Returns all registered users as safe summaries (id, full name, username and role).
     * Password hashes and expenses are never included.
     *
     * @return the list of all users, empty if none are registered
     */
    List<UserListDTO> getAllUsers();
}