package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.RegisterRequestDTO;
import com.muvs.inspection_system.dto.UserRequestDTO;
import com.muvs.inspection_system.dto.UserResponseDTO;
import com.muvs.inspection_system.dto.UserUpdateRequestDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO createUser(RegisterRequestDTO requestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);
    UserResponseDTO updateUserProfile(Long id, UserUpdateRequestDTO requestDTO);
    void deleteUser(Long id);
    UserResponseDTO changePassword(Long id, String oldPassword, String newPassword);
    void adminResetPassword(Long userId, String newPassword);
}
