package com.maurodelcore.expensetracker.repository;

import com.maurodelcore.expensetracker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Reads and writes {@link AppUser} rows. Spring Data creates the implementation
 * at startup, so only the custom lookup is declared here.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by their login name. Spring Data builds the query from the
     * method name.
     *
     * @param username the login name to look up
     * @return the matching user, or an empty {@code Optional} if no user has that username
     */
    Optional<AppUser> findByUsername(String username);
}