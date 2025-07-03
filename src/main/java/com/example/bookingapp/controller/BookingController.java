package com.example.bookingapp.controller;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.BookingSearchParametersDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "API for managing bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create booking",
            description = "Creates a new booking for the authenticated user")
    public BookingResponseDto create(@RequestBody @Valid CreateBookingDto dto) {
        return bookingService.create(dto);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user bookings",
            description = "Returns a list of bookings for the current user")
    public List<BookingResponseDto> getMyBookings() {
        return bookingService.findByCurrentUser();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get bookings with filters",
            description = "Returns a list of bookings by user ID and/or status (for managers only)")
    public List<BookingResponseDto> list(@RequestBody @Valid BookingSearchParametersDto params) {
        return bookingService.findAll(params);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get booking by ID",
            description = "Returns booking details for the owner or manager")
    public BookingResponseDto getById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update booking",
            description = "Updates booking details for the owner or manager")
    public BookingResponseDto update(@PathVariable Long id,
                                     @RequestBody @Valid BookingResponseDto dto) {
        return bookingService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel booking",
            description = "Cancels booking for the owner or manager")
    public void cancel(@PathVariable Long id) {
        bookingService.cancel(id);
    }
}
