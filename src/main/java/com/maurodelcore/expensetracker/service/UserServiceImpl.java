package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link UserService}, backed by {@link UserRepository}.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Creates the service with the repository it reads and writes users with.
     *
     * @param userRepository the repository that accesses the users table
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public AppUser saveUser(AppUser appUser) {
        return userRepository.save(appUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppUser findByUsername(String username) {
        // Return the user with the given username, or null if there is none
        return userRepository.findByUsername(username).orElse(null);
    }
}