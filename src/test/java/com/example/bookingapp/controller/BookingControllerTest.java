package com.example.bookingapp.controller;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.utils.CommonTestConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import java.time.LocalDate;
import java.util.List;
import static com.example.bookingapp.utils.BookingUtils.*;
import static com.example.bookingapp.utils.CommonTestConstants.CUSTOMER_ROLE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ADD_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BASE_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingControllerTest {
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
    @DisplayName("Get current user's bookings")
    @Sql(scripts = ADD_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getMyBookings_AuthenticatedUser_Success() throws Exception {
        // Given
        // Test data is set up via SQL script

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + MY_BOOKINGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<BookingResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<BookingResponseDto>>() {}
        );
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getId()).isEqualTo(VALID_BOOKING_ID);
        assertThat(actual.get(0).getAccommodationId()).isEqualTo(VALID_ACCOMMODATION_ID);
    }

    @Test
    @Transactional
    @WithMockUser(username = CommonTestConstants.ID, roles = CUSTOMER_ROLE)
    @DisplayName("Get booking by valid ID")
    @Sql(scripts = ADD_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getById_ValidId_Success() throws Exception {
        // Given
        // Test data is set up via SQL script

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + "/" + VALID_BOOKING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookingResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_BOOKING_ID);
        assertThat(actual.getAccommodationId()).isEqualTo(VALID_ACCOMMODATION_ID);
        assertThat(actual.getStatus()).isEqualTo(PENDING_STATUS);
    }

    @Test
    @WithMockUser(username = CommonTestConstants.ID, roles = CUSTOMER_ROLE)
    @DisplayName("Update booking with valid request")
    @Sql(scripts = ADD_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = CLEAR_BOOKING_TEST_DATA_SQL, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequest_Success() throws Exception {
        // Given
        BookingResponseDto updateDto = createBookingResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        // When
        MvcResult result = mockMvc.perform(put(BASE_URL + "/" + VALID_BOOKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookingResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
        assertThat(actual.getId()).isEqualTo(VALID_BOOKING_ID);
        assertThat(actual.getCheckInDate()).isEqualTo(LocalDate.of(2025, 7, 10));
        assertThat(actual.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }
}
