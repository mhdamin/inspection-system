package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDTO {

    private String accessToken;

    @Builder.Default
    private String type = "Bearer";

    private String refreshToken; // Optional: can return new refresh token for rotation
}
