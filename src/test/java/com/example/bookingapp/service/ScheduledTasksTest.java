package com.example.bookingapp.service;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.service.impl.ScheduledTasks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import java.util.Collections;
import java.util.List;
import static com.example.bookingapp.utils.BookingUtils.*;
import static com.example.bookingapp.utils.ScheduledUtils.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL, ADD_ACCOMMODATION_TEST_DATA_SQL, ADD_BOOKING_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {CLEAR_BOOKING_TEST_DATA_SQL, CLEAR_ACCOMMODATION_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ScheduledTasksTest {

    @Autowired
    private ScheduledTasks scheduledTasks;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @DisplayName("Check expired bookings with no expired bookings should send notification")
    void checkExpiredBookings_NoExpiredBookings_SendsNotification() {
        // Given
        when(bookingService.findExpiredBookings()).thenReturn(Collections.emptyList());

        // When
        scheduledTasks.checkExpiredBookings();

        // Then
        verify(notificationService).sendNotification(NO_OVERDUE_MESSAGE);
    }

    @Test
    @DisplayName("Check expired bookings with expired bookings should process them")
    void checkExpiredBookings_ExpiredBookings_ProcessesThem() {
        // Given
        Booking booking = createBookingEntity();
        when(bookingService.findExpiredBookings()).thenReturn(List.of(booking));

        // When
        scheduledTasks.checkExpiredBookings();

        // Then
        verify(bookingService).setExpired(booking.getId());
        verify(notificationService, never()).sendNotification(NO_OVERDUE_MESSAGE);
    }
}
