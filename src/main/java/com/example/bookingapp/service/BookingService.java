package com.example.bookingapp.service;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.model.Booking;
import java.util.List;

public interface BookingService {
    BookingResponseDto create(CreateBookingDto dto);

    List<BookingResponseDto> findByCurrentUser();

    List<BookingResponseDto> findAll(Long userId, String status);

    BookingResponseDto findById(Long id);

    BookingResponseDto update(Long id, BookingResponseDto dto);

    void cancel(Long id);

    List<Booking> findExpiredBookings();

    void setExpired(Long id);

}
