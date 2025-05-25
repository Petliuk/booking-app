package com.example.bookingapp.service;

import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    UserResponseDto getCurrentUser();

    UserResponseDto updateCurrentUser(UserRegistrationRequestDto requestDto);

    UserResponseDto updateRole(Long id, String roleName);
}
