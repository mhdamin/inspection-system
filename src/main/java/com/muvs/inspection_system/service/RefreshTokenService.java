package com.muvs.inspection_system.service;

import com.muvs.inspection_system.entity.RefreshToken;
import com.muvs.inspection_system.entity.User;

public interface RefreshTokenService {

    /**
     * Create a new refresh token for a user
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Validate and retrieve refresh token by token string
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Revoke a refresh token
     */
    void revokeRefreshToken(String token);

    /**
     * Revoke all refresh tokens for a user (on logout)
     */
    void revokeUserRefreshTokens(User user);

    /**
     * Delete expired refresh tokens (cleanup job)
     */
    void deleteExpiredTokens();
}
