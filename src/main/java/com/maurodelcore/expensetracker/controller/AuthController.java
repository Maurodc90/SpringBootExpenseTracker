package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.dto.AppUserDTO;
import com.maurodelcore.expensetracker.dto.AuthDTO;
import com.maurodelcore.expensetracker.dto.AuthResponseDTO;
import com.maurodelcore.expensetracker.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for creating an account and logging in. Both are public, as
 * configured in {@code SecurityConfig}, and both return a JWT on success.
 * Failures are not handled here: the service throws, and
 * {@code GlobalExceptionHandler} builds the error response.
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
     * If the username is already taken, the service throws
     * {@code UsernameAlreadyExistsException} and the client receives 409 Conflict instead.
     *
     * @param appUserDTO the full name, username and password sent in the request body
     * @return 201 Created with a response holding the token
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody AppUserDTO appUserDTO) {
        AuthResponseDTO response = authService.registerUser(appUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Checks the credentials of an existing user and returns a token.
     * If the credentials are wrong, Spring throws {@code BadCredentialsException}
     * and the client receives 401 Unauthorized instead.
     *
     * @param authDTO the username and password sent in the request body
     * @return 200 OK with a response holding the token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthDTO authDTO) {
        AuthResponseDTO response = authService.loginUser(authDTO);
        return ResponseEntity.ok(response);
    }

}