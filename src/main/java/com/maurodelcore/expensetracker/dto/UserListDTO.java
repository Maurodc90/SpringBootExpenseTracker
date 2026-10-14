package com.maurodelcore.expensetracker.dto;

import com.maurodelcore.expensetracker.model.Role;
import lombok.Data;

/**
 * Safe summary of a user for the admin user list. Contains only the fields an
 * administrator needs to see, and never the password hash or the user's expenses,
 * so it can be returned from the API without leaking sensitive data.
 */
@Data
public class UserListDTO {

    /** Database id of the user. */
    private Long id;

    /** Full name of the user, as given at sign-up. */
    private String fullName;

    /** Unique login name of the user. */
    private String username;

    /** Role of the user, either USER or ADMIN. */
    private Role role;
}