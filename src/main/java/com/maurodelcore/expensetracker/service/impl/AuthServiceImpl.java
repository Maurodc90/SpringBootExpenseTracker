package com.maurodelcore.expensetracker.service.impl;

import com.maurodelcore.expensetracker.dto.AppUserDTO;
import com.maurodelcore.expensetracker.dto.AuthDTO;
import com.maurodelcore.expensetracker.dto.AuthResponseDTO;
import com.maurodelcore.expensetracker.exception.UsernameAlreadyExistsException;
import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.model.Role;
import com.maurodelcore.expensetracker.security.JwtUtils;
import com.maurodelcore.expensetracker.service.AuthService;
import com.maurodelcore.expensetracker.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link AuthService}. Passwords are hashed before
 * saving, and logging in is delegated to Spring's {@link AuthenticationManager}.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Creates the service with everything it needs to register and log in users.
     *
     * @param userService           looks up and saves users in the database
     * @param passwordEncoder       hashes passwords at sign-up
     * @param authenticationManager checks credentials at login
     * @param jwtUtils              generates the token returned to the client
     */
    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponseDTO registerUser(AppUserDTO appUserDTO) {
        // Stop if the username already exists
        if (userService.findByUsername(appUserDTO.getUsername()) != null) {
            throw new UsernameAlreadyExistsException();
        }
        AppUser appUser = new AppUser();
        appUser.setFullName(appUserDTO.getFullName());
        appUser.setUsername(appUserDTO.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));  // store the hash, never the plain password
        appUser.setRole(Role.USER);
        userService.saveUser(appUser);

        // Log the new user in straight away to get a token
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(appUserDTO.getUsername());
        authDTO.setPassword(appUserDTO.getPassword());

        return loginUser(authDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponseDTO loginUser(AuthDTO authDTO) {
        // Throws BadCredentialsException if the username or password is wrong
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));
        final String token = jwtUtils.generateToken(authDTO.getUsername());
        return new AuthResponseDTO(token);
    }
}