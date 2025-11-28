package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.entity.RefreshToken;
import com.muvs.inspection_system.entity.User;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.RefreshTokenRepository;
import com.muvs.inspection_system.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private Long refreshTokenDuration;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user: {}", user.getUsername());

        // Delete any existing refresh tokens for this user (one token per user)
        refreshTokenRepository.deleteByUser(user);

        // Calculate expiry date
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(refreshTokenDuration / 1000);

        // Create new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created successfully for user: {}", user.getUsername());

        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        log.info("Verifying refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Refresh token not found: {}", token);
                    return new ResourceNotFoundException("Invalid refresh token");
                });

        if (refreshToken.isRevoked()) {
            log.error("Refresh token has been revoked: {}", token);
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            log.error("Refresh token has expired: {}", token);
            // Delete expired token
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired. Please login again");
        }

        log.info("Refresh token verified successfully");
        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token revoked successfully");
    }

    @Override
    @Transactional
    public void revokeUserRefreshTokens(User user) {
        log.info("Revoking all refresh tokens for user: {}", user.getUsername());
        refreshTokenRepository.deleteByUser(user);
        log.info("All refresh tokens revoked for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired refresh tokens deleted successfully");
    }
}
