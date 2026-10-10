package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Connects the database users to Spring Security. During login and on every
 * authenticated request, Spring calls this service to load a user by username.
 * It only loads the user; checking the password is done elsewhere.
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user from the database and converts it into the {@link UserDetails}
     * object that Spring Security works with.
     *
     * @param username the username to look up
     * @return the user's username and stored password hash, with no roles
     * @throws UsernameNotFoundException if no user has that username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // There are no roles yet, so an empty list of authorities is passed
        return new User(appUser.getUsername(), appUser.getPassword(), new ArrayList<>());

    }
}