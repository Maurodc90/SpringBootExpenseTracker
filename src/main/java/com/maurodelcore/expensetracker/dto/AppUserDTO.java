package com.maurodelcore.expensetracker.dto;

import lombok.Data;

/**
 * Data sent by a new user to sign up. The password arrives in plain text in the
 * request and is hashed by the service before anything is stored.
 */
@Data
public class AppUserDTO {

    /** The user's display name. */
    private String fullName;

    /** The unique name used to log in. */
    private String username;

    /** The plain-text password chosen by the user; never stored or returned. */
    private String password;

}