package com.example.bookingapp.controller;

import com.example.bookingapp.dto.user.RoleUpdateDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API for managing users and their roles")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user profile",
            description = "Returns the current authenticated user's profile data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public UserResponseDto getProfile() {
        return userService.getCurrentUser();
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user profile",
            description = "Updates the profile data of the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public UserResponseDto updateProfile(@RequestBody @Valid UserRegistrationRequestDto request) {
        return userService.updateCurrentUser(request);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Changes the user's role (available to managers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "400", description = "Invalid role"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestBody @Valid RoleUpdateDto roleUpdateDto) {
        return userService.updateRole(id, roleUpdateDto.getRoleName());
    }
}
