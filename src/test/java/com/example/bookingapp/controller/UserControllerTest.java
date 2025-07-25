package com.example.bookingapp.controller;

import com.example.bookingapp.dto.user.RoleUpdateDto;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.utils.CommonTestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static com.example.bookingapp.utils.CommonTestConstants.CUSTOMER_ROLE;
import static com.example.bookingapp.utils.CommonTestConstants.MANAGER_ROLE;
import static com.example.bookingapp.utils.UserUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ADD_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
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
    @WithMockUser(username = CommonTestConstants.ID, roles = CUSTOMER_ROLE)
    @DisplayName("Get current user's profile")
    void getProfile_AuthenticatedUser_Success() throws Exception {
        // Given
        // Test data is set up via SQL script

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + ME_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_USER_ID);
        assertThat(actual.getEmail()).isEqualTo(CUSTOMER_EMAIL);
        assertThat(actual.getFirstName()).isEqualTo(EXPECTED_FIRST_NAME);
    }

    @Test
    @WithMockUser(username = CommonTestConstants.ID, roles = CUSTOMER_ROLE)
    @DisplayName("Update current user's profile")
    void updateProfile_ValidRequest_Success() throws Exception {
        // Given
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put(BASE_URL + ME_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_USER_ID);
        assertThat(actual.getFirstName()).isEqualTo(EXPECTED_FIRST_NAME);
        assertThat(actual.getLastName()).isEqualTo(EXPECTED_LAST_NAME);
    }

    @Test
    @WithMockUser(authorities = MANAGER_ROLE)
    @DisplayName("Update user role with valid request")
    void updateRole_ValidRequest_Success() throws Exception {
        // Given
        RoleUpdateDto requestDto = createRoleUpdateDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put(BASE_URL + "/" + VALID_USER_ID + ROLE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_USER_ID);
        assertThat(actual.getEmail()).isEqualTo(CUSTOMER_EMAIL);
    }

    @Test
    @WithMockUser(roles = CUSTOMER_ROLE)
    @DisplayName("Update user role with unauthorized role")
    void updateRole_UnauthorizedRole_Fail() throws Exception {
        // Given
        RoleUpdateDto requestDto = createRoleUpdateDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(put(BASE_URL + "/" + VALID_USER_ID + ROLE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_FORBIDDEN);
    }
}
