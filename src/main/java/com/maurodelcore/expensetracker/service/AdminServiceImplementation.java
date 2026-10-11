package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link AdminService}, backed by the user repository.
 */
@Service
public class AdminServiceImplementation implements AdminService {

    private final UserRepository userRepository;

    /**
     * Creates the service with the repository it reads users from.
     *
     * @param userRepository the repository used to load users
     */
    public AdminServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}