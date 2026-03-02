package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.AdminPasswordResetDTO;
import com.muvs.inspection_system.dto.PasswordChangeRequestDTO;
import com.muvs.inspection_system.dto.UserResponseDTO;
import com.muvs.inspection_system.dto.UserUpdateRequestDTO;
import com.muvs.inspection_system.service.RoleService;
import com.muvs.inspection_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request,
            Authentication authentication) {
        log.info("PUT /api/users/{} - Updating user", id);

        try {
            // Get current user and target user
            UserResponseDTO currentUser = userService.getUserByUsername(authentication.getName());
            UserResponseDTO targetUser = userService.getUserById(id);

            // Check if current user can manage the target user
            if (!roleService.canManageUser(currentUser, targetUser)) {
                log.warn("User {} attempted to update user {} without permission", currentUser.getUsername(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to edit this user");
            }

            // Validate role assignments if roles are being updated
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                for (String role : request.getRoles()) {
                    if (!roleService.canAssignRole(currentUser, role)) {
                        log.warn("User {} attempted to assign role {} without permission", currentUser.getUsername(), role);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You do not have permission to assign role: " + role);
                    }
                }
            }

            UserResponseDTO updatedUser = userService.updateUserProfile(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("DELETE /api/users/{} - Deleting user", id);

        try {
            // Get current user and target user
            UserResponseDTO currentUser = userService.getUserByUsername(authentication.getName());
            UserResponseDTO targetUser = userService.getUserById(id);

            // Prevent self-deletion
            if (currentUser.getId().equals(id)) {
                log.warn("User {} attempted to delete their own account", currentUser.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You cannot delete your own account");
            }

            // Check if current user can manage the target user
            if (!roleService.canManageUser(currentUser, targetUser)) {
                log.warn("User {} attempted to delete user {} without permission", currentUser.getUsername(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to delete this user");
            }

            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<String> adminResetPassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminPasswordResetDTO request,
            Authentication authentication) {
        log.info("POST /api/users/{}/reset-password - Admin resetting password", id);

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match");
        }

        try {
            // Get current user and target user
            UserResponseDTO currentUser = userService.getUserByUsername(authentication.getName());
            UserResponseDTO targetUser = userService.getUserById(id);

            // Check if current user can manage the target user
            if (!roleService.canManageUser(currentUser, targetUser)) {
                log.warn("User {} attempted to reset password for user {} without permission", currentUser.getUsername(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to reset this user's password");
            }

            userService.adminResetPassword(id, request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/assignable-roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<String>> getAssignableRoles(Authentication authentication) {
        log.info("GET /api/users/assignable-roles - Getting assignable roles for user: {}", authentication.getName());

        UserResponseDTO currentUser = userService.getUserByUsername(authentication.getName());
        List<String> assignableRoles = roleService.getAssignableRoles(currentUser);

        return ResponseEntity.ok(assignableRoles);
    }
}
