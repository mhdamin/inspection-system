package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token; // Access token

    @Builder.Default
    private String type = "Bearer";

    private String username;
    private Set<String> roles;
    private String refreshToken; // Refresh token for obtaining new access tokens
}
