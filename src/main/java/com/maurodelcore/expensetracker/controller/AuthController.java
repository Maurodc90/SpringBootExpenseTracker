package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.dto.AppUserDTO;
import com.maurodelcore.expensetracker.dto.AuthDTO;
import com.maurodelcore.expensetracker.dto.AuthResponseDTO;
import com.maurodelcore.expensetracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for creating an account and logging in. Both are public, as
 * configured in {@code SecurityConfig}, and both return a JWT on success.
 */
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * Creates the controller with the service that holds the authentication logic.
     *
     * @param authService the service that registers and logs in users
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user and logs them in straight away.
     *
     * @param appUserDTO the full name, username and password sent in the request body
     * @return 200 with a response holding the token if the user was created,
     * otherwise 400 with the error message (for example, username already taken)
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody AppUserDTO appUserDTO) {
        AuthResponseDTO response = authService.registerUser(appUserDTO);
        // The service reports success through the message text, so the status is chosen from it
        if ("success".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Checks the credentials of an existing user and returns a token.
     *
     * @param authDTO the username and password sent in the request body
     * @return 200 with a response holding the token if the credentials are valid,
     * otherwise 400 with the error message
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthDTO authDTO) {
        AuthResponseDTO response = authService.loginUser(authDTO);
        if ("success".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

}