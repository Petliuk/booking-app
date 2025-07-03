package com.example.bookingapp.service;

import com.example.bookingapp.dto.user.ChangePasswordDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.dto.user.UserUpdateRequestDto;
import com.example.bookingapp.exception.RegistrationException;
import com.example.bookingapp.model.Role;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    UserResponseDto getCurrentUser();

    UserResponseDto updateCurrentUser(UserUpdateRequestDto requestDto);

    UserResponseDto updateRole(Long id, Role.RoleName roleName);

    UserResponseDto changePassword(ChangePasswordDto requestDto);
}
