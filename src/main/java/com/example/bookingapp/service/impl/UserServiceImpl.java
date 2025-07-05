package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.user.ChangePasswordDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.dto.user.UserUpdateRequestDto;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Default role " + Role.RoleName.CUSTOMER + "not found"));
        user.setRoles(Set.of(userRole));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getCurrentUser() {
        Long userId = Long.parseLong(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: "
                        + userId + " not found")));
    }

    @Override
    public UserResponseDto updateCurrentUser(UserUpdateRequestDto requestDto) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: "
                        + userId + " not found"));
        if (!user.getEmail().equals(requestDto.getEmail())
                && userRepository.existsByEmail(requestDto.getEmail())) {
            throw new InvalidRequestException("Email "
                    + requestDto.getEmail() + " is already taken");
        }
        User updatedUser = userMapper.toEntity(requestDto);
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateRole(Long id, Role.RoleName roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: "
                        + id));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.getRoles().clear();
        user.getRoles().add(role);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto changePassword(ChangePasswordDto requestDto) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: "
                        + userId + " not found"));
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        return userMapper.toDto(userRepository.save(user));
    }
}
