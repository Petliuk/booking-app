package com.example.bookingapp.service;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.utils.CommonTestConstants;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import static com.example.bookingapp.utils.BookingUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL, ADD_ACCOMMODATION_TEST_DATA_SQL, ADD_BOOKING_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {CLEAR_BOOKING_TEST_DATA_SQL, CLEAR_ACCOMMODATION_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(CommonTestConstants.DEFAULT_EMAIL);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Create booking with valid DTO should return saved DTO")
    void create_ValidDto_ReturnsSavedDto() {
        // Given
        CreateBookingDto requestDto = createBookingRequestDto();
        doNothing().when(paymentService).checkPendingPayments(anyLong());

        // When
        BookingResponseDto result = bookingService.create(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccommodationId()).isEqualTo(VALID_ACCOMMODATION_ID);
        assertThat(result.getStatus()).isEqualTo(PENDING_STATUS);
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    @DisplayName("Create booking with invalid dates should throw exception")
    void create_InvalidDates_ThrowsException() {
        // Given
        CreateBookingDto requestDto = createInvalidBookingRequestDto();
        doNothing().when(paymentService).checkPendingPayments(anyLong());

        // When & Then
        assertThrows(ConstraintViolationException.class, () -> bookingService.create(requestDto));
    }

    @Test
    @DisplayName("Find bookings by current user should return list")
    void findByCurrentUser_ValidUser_ReturnsList() {
        // When
        List<BookingResponseDto> result = bookingService.findByCurrentUser();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(VALID_USER_ID);
    }

    @Test
    @DisplayName("Find all bookings with status filter should return filtered list")
    void findAll_WithStatusFilter_ReturnsFilteredList() {
        // Given
        String status = PENDING_STATUS_STRING;

        // When
        List<BookingResponseDto> result = bookingService.findAll(null, status);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PENDING_STATUS);
    }

    @Test
    @DisplayName("Cancel booking as owner should succeed")
    void cancel_ValidBooking_Succeeds() {
        // Given
        Long bookingId = VALID_BOOKING_ID;

        // When
        bookingService.cancel(bookingId);

        // Then
        BookingResponseDto result = bookingService.findById(bookingId);
        assertThat(result.getStatus()).isEqualTo(CANCELED_STATUS);
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    @DisplayName("Cancel already canceled booking should throw exception")
    void cancel_AlreadyCanceled_ThrowsException() {
        // Given
        Long bookingId = VALID_BOOKING_ID;
        bookingService.cancel(bookingId);

        // When & Then
        assertThrows(InvalidRequestException.class, () -> bookingService.cancel(bookingId));
    }
}
