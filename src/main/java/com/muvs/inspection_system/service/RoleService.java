package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.UserResponseDTO;

import java.util.List;

public interface RoleService {
    boolean canManageUser(UserResponseDTO manager, UserResponseDTO target);
    boolean canAssignRole(UserResponseDTO manager, String targetRole);
    List<String> getAssignableRoles(UserResponseDTO manager);
    int getRoleLevel(String roleName);
    String getHighestRole(UserResponseDTO user);
}
