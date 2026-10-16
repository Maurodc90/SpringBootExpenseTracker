package com.maurodelcore.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data sent by a new user to sign up. The password arrives in plain text in the
 * request and is hashed by the service before anything is stored.
 */
@Data
public class AppUserDTO {

    /**
     * The user's display name.
     */
    @NotBlank(message = "fullName must not be blank")
    private String fullName;

    /**
     * The unique name used to log in.
     */
    @NotBlank(message = "username must not be blank")
    private String username;

    /**
     * The plain-text password chosen by the user; never stored or returned.
     */
    @NotBlank(message = "password must not be blank")
    private String password;

}