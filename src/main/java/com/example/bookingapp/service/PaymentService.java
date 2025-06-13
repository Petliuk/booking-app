package com.example.bookingapp.service;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    PaymentDto create(CreatePaymentDto dto);

    List<PaymentDto> findByUserId(Long userId);

    List<PaymentDto> findByCurrentUser();

    List<PaymentDto> findAll(Long userId);

    PaymentDto handleSuccess(String sessionId);

    PaymentDto handleCancel(String sessionId);

    List<Payment> findExpiredPayments();

    void checkPendingPayments(Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
