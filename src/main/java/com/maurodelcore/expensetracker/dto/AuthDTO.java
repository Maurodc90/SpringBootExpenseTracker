package com.maurodelcore.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Credentials sent by an existing user to log in. The password arrives in plain
 * text and is only compared against the stored hash, never stored.
 */
@Data
public class AuthDTO {

    /**
     * The name the user signed up with.
     */
    @NotBlank(message = "username must not be blank")
    private String username;

    /**
     * The plain-text password typed by the user.
     */
    @NotBlank(message = "password must not be blank")
    private String password;
}