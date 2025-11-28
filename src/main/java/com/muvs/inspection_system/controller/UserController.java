package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.PasswordChangeRequestDTO;
import com.muvs.inspection_system.dto.UserResponseDTO;
import com.muvs.inspection_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile(Authentication authentication) {
        log.info("GET /api/users/profile - Fetching profile for user: {}", authentication.getName());
        UserResponseDTO user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeRequestDTO request,
            Authentication authentication) {
        log.info("POST /api/users/change-password - Changing password for user: {}", authentication.getName());

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match");
        }

        try {
            UserResponseDTO user = userService.getUserByUsername(authentication.getName());
            userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
