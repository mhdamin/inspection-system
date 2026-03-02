package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.UserResponseDTO;
import com.muvs.inspection_system.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public boolean canManageUser(UserResponseDTO manager, UserResponseDTO target) {
        // Get the highest role level for both users
        int targetLevel = getRoleLevel(getHighestRole(target));

        // SUPERADMIN can manage anyone
        if (manager.getRoles().contains("ROLE_SUPERADMIN")) {
            return true;
        }

        // ADMIN can manage users below ADMIN level
        if (manager.getRoles().contains("ROLE_ADMIN")) {
            return targetLevel < 4; // Cannot manage ADMIN or SUPERADMIN
        }

        // Others cannot manage users
        return false;
    }

    @Override
    public boolean canAssignRole(UserResponseDTO manager, String targetRole) {
        int targetRoleLevel = getRoleLevel(targetRole);

        // SUPERADMIN can assign any role
        if (manager.getRoles().contains("ROLE_SUPERADMIN")) {
            return true;
        }

        // ADMIN can assign roles below ADMIN level
        if (manager.getRoles().contains("ROLE_ADMIN")) {
            return targetRoleLevel < 4; // Cannot assign ADMIN or SUPERADMIN
        }

        return false;
    }

    @Override
    public List<String> getAssignableRoles(UserResponseDTO manager) {
        List<String> assignableRoles = new ArrayList<>();

        if (manager.getRoles().contains("ROLE_SUPERADMIN")) {
            assignableRoles.add("ROLE_SUPERADMIN");
            assignableRoles.add("ROLE_ADMIN");
            assignableRoles.add("ROLE_MANAGER");
            assignableRoles.add("ROLE_INSPECTOR");
            assignableRoles.add("ROLE_USER");
        } else if (manager.getRoles().contains("ROLE_ADMIN")) {
            assignableRoles.add("ROLE_MANAGER");
            assignableRoles.add("ROLE_INSPECTOR");
            assignableRoles.add("ROLE_USER");
        }

        return assignableRoles;
    }

    @Override
    public int getRoleLevel(String roleName) {
        return switch (roleName) {
            case "ROLE_SUPERADMIN" -> 5;
            case "ROLE_ADMIN" -> 4;
            case "ROLE_MANAGER" -> 3;
            case "ROLE_INSPECTOR" -> 2;
            case "ROLE_USER" -> 1;
            default -> 0;
        };
    }

    @Override
    public String getHighestRole(UserResponseDTO user) {
        Set<String> roles = user.getRoles();
        int highestLevel = 0;
        String highestRole = "ROLE_USER";

        for (String role : roles) {
            int level = getRoleLevel(role);
            if (level > highestLevel) {
                highestLevel = level;
                highestRole = role;
            }
        }

        return highestRole;
    }
}
