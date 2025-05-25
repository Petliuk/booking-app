package com.example.bookingapp.service;

import com.example.bookingapp.dto.booking.BookingDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.model.Booking;
import java.util.List;

public interface BookingService {
    BookingDto create(CreateBookingDto dto);

    List<BookingDto> findByCurrentUser();

    List<BookingDto> findAll(Long userId, String status);

    BookingDto findById(Long id);

    BookingDto update(Long id, BookingDto dto);

    void cancel(Long id);

    List<Booking> findExpiredBookings();

    void setExpired(Long id);

}
