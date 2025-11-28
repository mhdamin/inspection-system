package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.config.JwtUtil;
import com.muvs.inspection_system.dto.*;
import com.muvs.inspection_system.entity.RefreshToken;
import com.muvs.inspection_system.entity.User;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.UserRepository;
import com.muvs.inspection_system.service.RefreshTokenService;
import com.muvs.inspection_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Get user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate access token (15 minutes)
        String accessToken = jwtUtil.generateToken(userDetails);

        // Get user entity for refresh token
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create refresh token (7 days)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Get user roles
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Build response
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(accessToken)
                .type("Bearer")
                .username(userDetails.getUsername())
                .roles(roles)
                .refreshToken(refreshToken.getToken())
                .build();

        log.info("Login successful for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());
        UserResponseDTO user = userService.createUser(registerRequest);
        log.info("Registration successful for user: {}", registerRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Token refresh request received");

        // Verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        // Get user from refresh token
        User user = refreshToken.getUser();

        // Create UserDetails for token generation
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getName())
                        .toArray(String[]::new))
                .build();

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(userDetails);

        // Build response
        RefreshTokenResponseDTO response = RefreshTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .type("Bearer")
                .refreshToken(request.getRefreshToken()) // Return same refresh token
                .build();

        log.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Logout request received");

        try {
            // Revoke refresh token
            refreshTokenService.revokeRefreshToken(request.getRefreshToken());
            log.info("Logout successful - refresh token revoked");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Logout attempt with invalid or expired token");
            // Still return success even if token is invalid (already logged out)
            return ResponseEntity.ok().build();
        }
    }
}
