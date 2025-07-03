package com.example.bookingapp.utils;

import com.example.bookingapp.dto.user.RoleUpdateDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.model.Role;
import com.example.bookingapp.model.User;

import static com.example.bookingapp.model.Role.RoleName.MANAGER;

public class UserUtils implements CommonTestConstants {
    public static final String BASE_URL = "/users";
    public static final String ME_ENDPOINT = "/me";
    public static final String ROLE_ENDPOINT = "/role";
    public static final Long VALID_USER_ID = 1L;
    public static final String CUSTOMER_EMAIL = "user@example.com";
    public static final String EXPECTED_FIRST_NAME = "John";
    public static final String EXPECTED_LAST_NAME = "Doe";
    public static final String ADD_BASE_TEST_DATA_SQL = "classpath:database/common/add-base-test-data.sql";
    public static final String CLEAR_BASE_TEST_DATA_SQL = "classpath:database/common/clear-base-test-data.sql";
    public static final int STATUS_FORBIDDEN = 403;
    public static final String INVALID_EMAIL = "invalid@example.com";
    public static final Role.RoleName CUSTOMER_ROLE_NAME = Role.RoleName.CUSTOMER;
    public static final String NEW_USER_EMAIL = "newuser@example.com";
    public static final String NEW_USER_PASSWORD = "password123";
    public static final String NEW_USER_FIRST_NAME = "Alice";
    public static final String NEW_USER_LAST_NAME = "Brown";
    public static final Role.RoleName MANAGER_ROLE_NAME = MANAGER;

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return createUserRegistrationRequestDto(DEFAULT_EMAIL, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto(String email, String firstName, String lastName) {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail(email);
        dto.setPassword(DEFAULT_PASSWORD);
        dto.setRepeatPassword(DEFAULT_PASSWORD);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        return dto;
    }

    public static RoleUpdateDto createRoleUpdateDto() {
        return createRoleUpdateDto(MANAGER_ROLE_NAME);
    }

    public static RoleUpdateDto createRoleUpdateDto(Role.RoleName roleName) {
        RoleUpdateDto dto = new RoleUpdateDto();
        dto.setRoleName(roleName);
        return dto;
    }

    public static User createUserEntity() {
        return createUserEntity(NEW_USER_EMAIL, NEW_USER_PASSWORD, NEW_USER_FIRST_NAME, NEW_USER_LAST_NAME);
    }

    public static User createUserEntity(String email, String password, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
