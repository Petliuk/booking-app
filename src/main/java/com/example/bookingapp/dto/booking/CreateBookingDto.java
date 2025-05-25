package com.example.bookingapp.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBookingDto {
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @NotNull(message = "Arrival date is required")
    private LocalDate checkInDate;

    @Future(message = "The departure date must be in the future.")
    @NotNull(message = "Departure date is required.")
    private LocalDate checkOutDate;

    @NotNull(message = "Housing ID is required")
    private Long accommodationId;
}
