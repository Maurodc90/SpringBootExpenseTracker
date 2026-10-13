package com.maurodelcore.expensetracker.dto;

import lombok.Data;

/**
 * Response returned by the sign-up and login endpoints when they succeed. It carries
 * the JWT the client must send in later requests. Failures never produce this
 * response: the service throws, and {@code GlobalExceptionHandler} builds the error response.
 */
@Data
public class AuthResponseDTO {

    /**
     * The JWT the client sends in the {@code Authorization} header as {@code Bearer <token>}.
     */
    private String token;

    /**
     * Creates a response holding the token.
     *
     * @param token the JWT generated for the authenticated user
     */
    public AuthResponseDTO(String token) {
        this.token = token;
    }

}