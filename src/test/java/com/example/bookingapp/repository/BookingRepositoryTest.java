package com.example.bookingapp.repository;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.booking.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static com.example.bookingapp.utils.BookingUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL, ADD_BOOKING_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BOOKING_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Find bookings by user ID should return list of bookings")
    void findByUserId_ValidUserId_ReturnsBookings() {
        // Given
        Long userId = VALID_USER_ID;

        // When
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        // Then
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getUser().getId()).isEqualTo(userId);
        assertThat(bookings.get(0).getStatus()).isEqualTo(PENDING_STATUS);
    }

    @Test
    @DisplayName("Exists by accommodation ID and status not canceled should return true")
    void existsByAccommodationIdAndStatusNot_ValidAccommodationId_ReturnsTrue() {
        // Given
        Long accommodationId = VALID_ACCOMMODATION_ID;

        // When
        boolean exists = bookingRepository.existsByAccommodationIdAndStatusNot(
                accommodationId, CANCELED_STATUS);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Find bookings by status should return list of bookings")
    void findByStatus_ValidStatus_ReturnsBookings() {
        // Given
        Booking.BookingStatus status = PENDING_STATUS;

        // When
        List<Booking> bookings = bookingRepository.findByStatus(status);

        // Then
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Count bookings with overlapping dates should return correct count")
    void countByAccommodationIdAndStatusNotAndDateOverlap_ValidParams_ReturnsCount() {
        // Given
        Long accommodationId = VALID_ACCOMMODATION_ID;
        LocalDate checkInDate = TEST_CHECK_IN_DATE;
        LocalDate checkOutDate = TEST_CHECK_OUT_DATE;

        // When
        long count = bookingRepository.countByAccommodationIdAndStatusNotAndDateOverlap(
                accommodationId, CANCELED_STATUS, checkInDate, checkOutDate);

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Save booking should persist entity")
    void save_ValidBooking_SavesEntity() {
        // Given
        Booking booking = createBookingEntity();

        // When
        Booking saved = bookingRepository.save(booking);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Booking> found = bookingRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
