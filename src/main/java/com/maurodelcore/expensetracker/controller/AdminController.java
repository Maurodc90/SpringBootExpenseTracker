package com.maurodelcore.expensetracker.controller;

import com.maurodelcore.expensetracker.model.AppUser;
import com.maurodelcore.expensetracker.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for administrator operations under {@code /admin}.
 * Access is restricted to users with the ADMIN role in the security configuration.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Creates the controller with the service that holds the admin logic.
     *
     * @param adminService the service used for administrator operations
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Lists all registered users.
     *
     * @return 200 OK with the list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}