package com.example.bookingapp.controller;

import com.example.bookingapp.dto.user.ChangePasswordDto;
import com.example.bookingapp.dto.user.RoleUpdateDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.dto.user.UserUpdateRequestDto;
import com.example.bookingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
    public UserResponseDto getProfile() {
        return userService.getCurrentUser();
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user profile",
            description = "Updates the profile data of the current user")
    public UserResponseDto updateProfile(@RequestBody @Valid UserUpdateRequestDto request) {
        return userService.updateCurrentUser(request);
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change user password",
            description = "Changes the password of the current "
                    + "user after verifying the old password")
    public UserResponseDto changePassword(@RequestBody @Valid ChangePasswordDto request) {
        return userService.changePassword(request);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Changes the user's role (available to managers only)")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestBody @Valid RoleUpdateDto dto) {
        return userService.updateRole(id, dto.getRoleName());
    }
}
