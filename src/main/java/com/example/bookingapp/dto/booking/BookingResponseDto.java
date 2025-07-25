package com.example.bookingapp.dto.booking;

import com.example.bookingapp.model.Booking;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private Booking.BookingStatus status;
}
