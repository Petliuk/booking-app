package com.example.bookingapp.service;

import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.exception.RegistrationException;
import com.example.bookingapp.model.Role;
import com.example.bookingapp.utils.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import static com.example.bookingapp.utils.UserUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BASE_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(CommonTestConstants.ID);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    @DisplayName("Register user with valid DTO should return DTO")
    void register_ValidDto_ReturnsDto() {
        // Given
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                NEW_USER_EMAIL, NEW_USER_FIRST_NAME, NEW_USER_LAST_NAME);

        // When
        UserResponseDto result = userService.register(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(NEW_USER_EMAIL);
        assertThat(result.getFirstName()).isEqualTo(NEW_USER_FIRST_NAME);
    }

    @Test
    @DisplayName("Register user with existing email should throw exception")
    void register_ExistingEmail_ThrowsException() {
        // Given
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                CommonTestConstants.DEFAULT_EMAIL, EXPECTED_FIRST_NAME, EXPECTED_LAST_NAME);

        // When & Then
        assertThrows(RegistrationException.class, () -> userService.register(requestDto));
    }

    @Test
    @DisplayName("Get current user should return DTO")
    void getCurrentUser_Valid_ReturnsDto() {
        // When
        UserResponseDto result = userService.getCurrentUser();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(CommonTestConstants.DEFAULT_EMAIL);
    }

    @Test
    @DisplayName("Update user role should return updated DTO")
    void updateRole_ValidRole_ReturnsUpdatedDto() {
        // Given
        Long userId = VALID_USER_ID;
        Role.RoleName roleName = MANAGER_ROLE_NAME;

        // When
        UserResponseDto result = userService.updateRole(userId, roleName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).contains(String.valueOf(roleName));
    }

    @Test
    @DisplayName("Update role with invalid role name should throw exception")
    void updateRole_InvalidRoleName_ThrowsException() {
        // Given
        Long userId = VALID_USER_ID;
        String invalidRoleName = "INVALID_ROLE";

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateRole(userId, Role.RoleName.valueOf(invalidRoleName)));
    }
}
