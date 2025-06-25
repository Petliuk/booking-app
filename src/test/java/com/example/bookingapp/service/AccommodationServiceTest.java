package com.example.bookingapp.service;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import static com.example.bookingapp.utils.AccommodationUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL, ADD_ACCOMMODATION_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccommodationServiceTest {
    @Autowired
    private AccommodationService accommodationService;

    @Test
    @DisplayName("Create accommodation with valid DTO should return saved DTO")
    void create_ValidDto_ReturnsSavedDto() {
        // Given
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto();

        // When
        AccommodationDto result = accommodationService.create(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPropertyType()).isEqualTo(HOUSE_PROPERTY_TYPE);
        assertThat(result.getLocation().getCity()).isEqualTo(KYIV_CITY);
        assertThat(result.getPricePerDay()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Create accommodation with null DTO should throw exception")
    void create_NullDto_ThrowsException() {
        // Given
        CreateAccommodationRequestDto requestDto = null;

        // When & Then
        assertThrows(InvalidRequestException.class, () -> accommodationService.create(requestDto));
    }

    @Test
    @DisplayName("Find accommodation by invalid ID should throw exception")
    void findById_InvalidId_ThrowsException() {
        // Given
        Long invalidId = INVALID_ACCOMMODATION_ID;

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> accommodationService.findById(invalidId));
    }

    @Test
    @DisplayName("Delete accommodation with no active bookings should succeed")
    void delete_NoActiveBookings_Succeeds() {
        // Given
        Long validId = VALID_ACCOMMODATION_ID;

        // When
        accommodationService.delete(validId);

        // Then
        assertThrows(EntityNotFoundException.class, () -> accommodationService.findById(validId));
    }

}
