package com.maurodelcore.expensetracker.dto;

import lombok.Data;

/**
 * Response returned by the sign-up and login endpoints. On success it carries the
 * JWT and the message "success"; on failure the token is {@code null} and the
 * message explains what went wrong.
 */
@Data
public class AuthResponseDTO {

    /** The JWT the client sends in the {@code Authorization} header, or {@code null} on failure. */
    private String token;

    /** "success" or a short error message for the client. */
    private String message;

    /**
     * Creates a response.
     *
     * @param token   the JWT, or {@code null} if the request failed
     * @param message "success" or the error message
     */
    public AuthResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }
}