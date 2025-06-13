package com.example.bookingapp.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentDto {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
}
