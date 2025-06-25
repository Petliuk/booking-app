package com.example.bookingapp.repository;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.accommodation.AccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;
import static com.example.bookingapp.utils.AccommodationUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_ACCOMMODATION_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_ACCOMMODATION_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccommodationRepositoryTest {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    @DisplayName("Find accommodation by valid ID should return accommodation")
    void findById_ValidId_ReturnsAccommodation() {
        // Given
        Long validId = VALID_ACCOMMODATION_ID;

        // When
        Optional<Accommodation> result = accommodationRepository.findById(validId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(validId);
        assertThat(result.get().getPropertyType()).isEqualTo(APARTMENT_PROPERTY_TYPE);
        assertThat(result.get().getLocation().getCity()).isEqualTo(EXPECTED_CITY);
        assertThat(result.get().getPricePerDay()).isEqualTo(EXPECTED_PRICE);
    }

    @Test
    @DisplayName("Find accommodation by invalid ID should return empty optional")
    void findById_InvalidId_ReturnsEmpty() {
        // Given
        Long invalidId = INVALID_ACCOMMODATION_ID;

        // When
        Optional<Accommodation> result = accommodationRepository.findById(invalidId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Save accommodation should persist entity")
    void save_ValidAccommodation_SavesEntity() {
        // Given
        Accommodation accommodation = createAccommodationEntity();

        // When
        Accommodation saved = accommodationRepository.save(accommodation);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Accommodation> found = accommodationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPropertyType()).isEqualTo(HOUSE_PROPERTY_TYPE);
    }

    @Test
    @DisplayName("Delete accommodation by ID should remove entity")
    void deleteById_ValidId_RemovesAccommodation() {
        // Given
        Long validId = VALID_ACCOMMODATION_ID;

        // When
        accommodationRepository.deleteById(validId);

        // Then
        Optional<Accommodation> result = accommodationRepository.findById(validId);
        assertThat(result).isEmpty();
    }
}
