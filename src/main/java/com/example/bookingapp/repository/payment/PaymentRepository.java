package com.example.bookingapp.repository.payment;

import com.example.bookingapp.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findBySessionId(String sessionId);

    Long countByStatusAndBookingUserId(Payment.PaymentStatus status, Long userId);
}
