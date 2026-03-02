package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.RegisterRequestDTO;
import com.muvs.inspection_system.dto.UserRequestDTO;
import com.muvs.inspection_system.dto.UserResponseDTO;
import com.muvs.inspection_system.dto.UserUpdateRequestDTO;
import com.muvs.inspection_system.entity.Role;
import com.muvs.inspection_system.entity.User;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.RoleRepository;
import com.muvs.inspection_system.repository.UserRepository;
import com.muvs.inspection_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(RegisterRequestDTO requestDTO) {
        // Check if username already exists
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + requestDTO.getUsername());
        }

        // Get roles
        Set<Role> roles = new HashSet<>();
        if (requestDTO.getRoles() == null || requestDTO.getRoles().isEmpty()) {
            // Default to ROLE_USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_USER"));
            roles.add(userRole);
        } else {
            for (String roleName : requestDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        // Create user
        User user = User.builder()
                .username(requestDTO.getUsername())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .roles(roles)
                .build();

        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update username if changed and not duplicate
        if (!user.getUsername().equals(requestDTO.getUsername())) {
            if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists: " + requestDTO.getUsername());
            }
            user.setUsername(requestDTO.getUsername());
        }

        // Update password if provided
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        // Update roles if provided
        if (requestDTO.getRoles() != null && !requestDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : requestDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(Long id, UserUpdateRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update username if changed and not duplicate
        if (requestDTO.getUsername() != null && !user.getUsername().equals(requestDTO.getUsername())) {
            if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists: " + requestDTO.getUsername());
            }
            user.setUsername(requestDTO.getUsername());
        }

        // Update roles if provided (admin only)
        if (requestDTO.getRoles() != null && !requestDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : requestDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    @Transactional
    public void adminResetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Admin can reset password without knowing old password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
