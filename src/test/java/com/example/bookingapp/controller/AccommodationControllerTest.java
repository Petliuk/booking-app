package com.example.bookingapp.controller;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.model.Accommodation;
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
import java.math.BigDecimal;
import static com.example.bookingapp.model.Accommodation.PropertyType.HOUSE;
import static com.example.bookingapp.utils.AccommodationUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ADD_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccommodationControllerTest {
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
    @WithMockUser(username = CommonTestConstants.ID, authorities = MANAGER_ROLE)
    @DisplayName("Create accommodation with valid request")
    @Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void create_ValidRequest_Success() throws Exception {
        // Given
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        AccommodationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), AccommodationDto.class);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getPropertyType()).isEqualTo(HOUSE);
        assertThat(actual.getLocation().getStreet()).isEqualTo("123 Main St");
        assertThat(actual.getPricePerDay()).isEqualTo(new BigDecimal("100.00"));
        assertThat(actual.getAvailability()).isEqualTo(5);
    }

    @Test
    @WithMockUser(username = CUSTOMER_USERNAME, roles = CUSTOMER_ROLE)
    @DisplayName("Create accommodation with unauthorized role")
    void create_UnauthorizedRole_Fail() throws Exception {
        // Given
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_FORBIDDEN);
    }

    @Test
    @WithMockUser(username = CUSTOMER_USERNAME, roles = CUSTOMER_ROLE)
    @DisplayName("Get accommodation by invalid ID")
    void getById_InvalidId_Fail() throws Exception {
        // Given
        // No specific setup needed for invalid ID

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + "/" + INVALID_ACCOMMODATION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_NOT_FOUND);
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME, authorities = MANAGER_ROLE)
    @DisplayName("Update accommodation with valid request")
    @Sql(scripts = ADD_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequest_Success() throws Exception {
        // Given
        AccommodationDto updateDto = createAccommodationDto();
        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        // When
        MvcResult result = mockMvc.perform(put(BASE_URL + "/" + VALID_ACCOMMODATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        AccommodationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), AccommodationDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_ACCOMMODATION_ID);
        assertThat(actual.getPropertyType()).isEqualTo(Accommodation.PropertyType.CONDO);
        assertThat(actual.getLocation().getStreet()).isEqualTo("456 New St");
        assertThat(actual.getPricePerDay()).isEqualTo(new BigDecimal("150.00"));
        assertThat(actual.getAvailability()).isEqualTo(3);
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME, authorities = MANAGER_ROLE)
    @DisplayName("Delete accommodation with valid ID")
    @Sql(scripts = ADD_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidId_Success() throws Exception {
        // Given
        // Test data is set up via SQL script

        // When
        MvcResult result = mockMvc.perform(delete(BASE_URL + "/" + VALID_ACCOMMODATION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_NO_CONTENT);
    }

    @Test
    @WithMockUser(username = CUSTOMER_USERNAME, roles = CUSTOMER_ROLE)
    @DisplayName("Delete accommodation with unauthorized role")
    @Sql(scripts = ADD_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_UnauthorizedRole_Fail() throws Exception {
        // Given
        // Test data is set up via SQL script

        // When
        MvcResult result = mockMvc.perform(delete(BASE_URL + "/" + VALID_ACCOMMODATION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_FORBIDDEN);
    }

    @Test
    @WithMockUser(username = MANAGER_USERNAME, authorities = MANAGER_ROLE)
    @DisplayName("Create accommodation with invalid request")
    void create_InvalidRequest_Fail() throws Exception {
        // Given
        CreateAccommodationRequestDto requestDto = createInvalidAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        assertThat(result.getResponse().getStatus()).isEqualTo(STATUS_BAD_REQUEST);
    }
}
