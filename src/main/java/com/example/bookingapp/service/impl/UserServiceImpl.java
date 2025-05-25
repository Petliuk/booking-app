package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.exception.RegistrationException;
import com.example.bookingapp.mapper.UserMapper;
import com.example.bookingapp.model.Role;
import com.example.bookingapp.model.User;
import com.example.bookingapp.repository.role.RoleRepository;
import com.example.bookingapp.repository.user.UserRepository;
import com.example.bookingapp.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email: "
                    + requestDto.getEmail() + " is already taken");
        }
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role userRole = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Default role CUSTOMER not found"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateCurrentUser(UserRegistrationRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        Role.RoleName roleNameEnum;
        try {
            roleNameEnum = Role.RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid role name: "
                    + roleName + ". Must be one of: CUSTOMER, MANAGER");
        }
        Role role = roleRepository.findByName(roleNameEnum)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleNameEnum));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
