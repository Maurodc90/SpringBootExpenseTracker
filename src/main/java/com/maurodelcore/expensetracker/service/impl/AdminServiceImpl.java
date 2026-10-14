package com.maurodelcore.expensetracker.service.impl;

import com.maurodelcore.expensetracker.dto.UserListDTO;
import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.repository.UserRepository;
import com.maurodelcore.expensetracker.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link AdminService}, backed by the user repository.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * Creates the service with the repository it reads users from.
     *
     * @param userRepository the repository used to load users
     */
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Each stored user is copied into a {@link UserListDTO} holding only the safe
     * fields, so the password hash never leaves the service layer.
     */
    @Override
    public List<UserListDTO> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        List<UserListDTO> result = new ArrayList<>();
        for (AppUser appUser : users) {
            // Copy only the safe fields; the password hash is deliberately left out
            UserListDTO dto = new UserListDTO();
            dto.setId(appUser.getId());
            dto.setUsername(appUser.getUsername());
            dto.setFullName(appUser.getFullName());
            dto.setRole(appUser.getRole());
            result.add(dto);
        }
        return result;
    }
}