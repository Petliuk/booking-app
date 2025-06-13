package com.example.bookingapp.dto.payment;

import com.example.bookingapp.model.Payment;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private Payment.PaymentStatus status;
    private Long bookingId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
