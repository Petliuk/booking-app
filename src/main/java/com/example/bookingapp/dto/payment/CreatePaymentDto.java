package com.example.bookingapp.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePaymentDto {
    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be a positive number")
    private Long bookingId;
}
