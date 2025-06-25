package com.example.bookingapp.repository;

import com.example.bookingapp.model.Payment;
import com.example.bookingapp.repository.payment.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;
import static com.example.bookingapp.utils.PaymentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {CLEAR_BASE_TEST_DATA_SQL, ADD_BASE_TEST_DATA_SQL, ADD_BOOKING_TEST_DATA_SQL, ADD_PAYMENT_TEST_DATA_SQL},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = CLEAR_PAYMENT_TEST_DATA_SQL,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Find payment by session ID should return payment")
    void findBySessionId_ValidSessionId_ReturnsPayment() {
        // Given
        String sessionId = VALID_SESSION_ID;

        // When
        Optional<Payment> result = paymentRepository.findBySessionId(sessionId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSessionId()).isEqualTo(sessionId);
        assertThat(result.get().getStatus()).isEqualTo(PENDING_STATUS);
    }

    @Test
    @DisplayName("Find payment by invalid session ID should return empty optional")
    void findBySessionId_InvalidSessionId_ReturnsEmpty() {
        // Given
        String invalidSessionId = INVALID_SESSION_ID;

        // When
        Optional<Payment> result = paymentRepository.findBySessionId(invalidSessionId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Save payment should persist entity")
    void save_ValidPayment_SavesEntity() {
        // Given
        Payment payment = createPaymentEntity();

        // When
        Payment saved = paymentRepository.save(payment);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Payment> found = paymentRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
