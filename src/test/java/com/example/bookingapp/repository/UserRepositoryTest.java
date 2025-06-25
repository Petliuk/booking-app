package com.example.bookingapp.repository;

import com.example.bookingapp.model.User;
import com.example.bookingapp.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;
import static com.example.bookingapp.utils.UserUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_BASE_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by email should return user with roles")
    void findByEmail_ValidEmail_ReturnsUserWithRoles() {
        // Given
        String email = CUSTOMER_EMAIL;

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getRoles()).isNotEmpty();
        assertThat(result.get().getRoles().stream()
                .anyMatch(r -> r.getName() == CUSTOMER_ROLE_NAME)).isTrue();
    }

    @Test
    @DisplayName("Find user by invalid email should return empty optional")
    void findByEmail_InvalidEmail_ReturnsEmpty() {
        // Given
        String invalidEmail = INVALID_EMAIL;

        // When
        Optional<User> result = userRepository.findByEmail(invalidEmail);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Exists by email should return true for existing email")
    void existsByEmail_ValidEmail_ReturnsTrue() {
        // Given
        String email = CUSTOMER_EMAIL;

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Save user should persist entity")
    void save_ValidUser_SavesEntity() {
        // Given
        User user = createUserEntity();

        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
