package com.example.bookingapp.controller;

import com.example.bookingapp.dto.UserLoginRequestDto;
import com.example.bookingapp.dto.UserLoginResponseDto;
import com.example.bookingapp.dto.UserRegistrationRequestDto;
import com.example.bookingapp.dto.UserResponseDto;
import com.example.bookingapp.exception.RegistrationException;
import com.example.bookingapp.security.AuthenticationService;
import com.example.bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication",
        description = "Endpoints for user authentication and registration")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user in the system and returns user details")
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "User login",
            description = "Authenticates a user and returns an access token")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    /* POST: /register - Allows users to register a new account.
        POST: /login - Grants JWT tokens to authenticated users.*/
}
