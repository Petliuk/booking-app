package com.example.bookingapp.dto.payment;

import com.example.bookingapp.model.Booking;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentCreationDto {
    private Booking booking;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
