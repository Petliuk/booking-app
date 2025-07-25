package com.example.bookingapp.annotation.validation;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDate checkInDate = null;
        LocalDate checkOutDate = null;

        if (value instanceof CreateBookingDto) {
            CreateBookingDto dto = (CreateBookingDto) value;
            checkInDate = dto.getCheckInDate();
            checkOutDate = dto.getCheckOutDate();
        } else if (value instanceof BookingResponseDto) {
            BookingResponseDto dto = (BookingResponseDto) value;
            checkInDate = dto.getCheckInDate();
            checkOutDate = dto.getCheckOutDate();
        }

        return checkInDate != null && checkOutDate != null
                && !checkOutDate.isBefore(checkInDate)
                && !checkOutDate.isEqual(checkInDate);
    }
}
