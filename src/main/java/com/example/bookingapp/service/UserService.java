package com.example.bookingapp.service;

import com.example.bookingapp.dto.UserRegistrationRequestDto;
import com.example.bookingapp.dto.UserResponseDto;
import com.example.bookingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
