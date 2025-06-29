package com.example.bookingapp.controller;

import com.example.bookingapp.dto.user.UserLoginRequestDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static com.example.bookingapp.utils.AuthUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Register user with valid request")
    @Sql(scripts = CLEAN_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void registerUser_ValidRequest_Success() throws Exception {
        // Given
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL + REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(NEW_USER_EMAIL);
        assertThat(actual.getFirstName()).isEqualTo(NEW_USER_FIRST_NAME);
        assertThat(actual.getLastName()).isEqualTo(NEW_USER_LAST_NAME);
    }

    @Test
    @DisplayName("Register user with duplicate email")
    @Sql(scripts = LOGIN_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void registerUser_DuplicateEmail_Fail() throws Exception {
        // Given
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(DEFAULT_EMAIL, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL + REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_CONFLICT);
    }

    @Test
    @DisplayName("Login with invalid credentials")
    @Sql(scripts = LOGIN_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_InvalidCredentials_Fail() throws Exception {
        // Given
        UserLoginRequestDto requestDto = createUserLoginRequestDto();
        requestDto.setPassword(INVALID_PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL + LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_UNAUTHORIZED);
    }
}
