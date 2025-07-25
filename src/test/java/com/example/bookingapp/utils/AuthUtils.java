package com.example.bookingapp.utils;

import com.example.bookingapp.dto.user.UserLoginRequestDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;

public class AuthUtils implements CommonTestConstants {

    public static final String BASE_URL = "/auth";
    public static final String REGISTER_ENDPOINT = "/register";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String CLEAN_TEST_DATA_SQL = "classpath:database/register/clean-test-data.sql";
    public static final String LOGIN_TEST_DATA_SQL = "classpath:database/login/login-test-data.sql";
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_CONFLICT = 409;
    public static final String INVALID_PASSWORD = "wrongpassword";
    public static final String NEW_USER_EMAIL = "newuser@example.com";
    public static final String NEW_USER_FIRST_NAME = "Alice";
    public static final String NEW_USER_LAST_NAME = "Brown";

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return createUserRegistrationRequestDto(
                NEW_USER_EMAIL,
                NEW_USER_FIRST_NAME,
                NEW_USER_LAST_NAME
        );
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

    public static UserLoginRequestDto createUserLoginRequestDto() {
        return createUserLoginRequestDto(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static UserLoginRequestDto createUserLoginRequestDto(String email, String password) {
        UserLoginRequestDto dto = new UserLoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }
}
