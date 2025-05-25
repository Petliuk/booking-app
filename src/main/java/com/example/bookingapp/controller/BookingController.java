package com.example.bookingapp.controller;

import com.example.bookingapp.dto.booking.BookingDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid data or accommodation unavailable"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public BookingDto create(@RequestBody @Valid CreateBookingDto dto) {
        return bookingService.create(dto);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user bookings",
            description = "Returns a list of bookings for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of bookings"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public List<BookingDto> getMyBookings() {
        return bookingService.findByCurrentUser();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get bookings with filters",
            description = "Returns a list of bookings by user ID and/or status (for managers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of bookings"),
            @ApiResponse(responseCode = "403", description = "Access forbidden"),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public List<BookingDto> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        return bookingService.findAll(userId, status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get booking by ID",
            description = "Returns booking details for the owner or manager")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access forbidden"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public BookingDto getById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update booking",
            description = "Updates booking details for the owner or manager")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access forbidden"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public BookingDto update(@PathVariable Long id, @RequestBody @Valid BookingDto dto) {
        return bookingService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel booking",
            description = "Cancels booking for the owner or manager")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking canceled"),
            @ApiResponse(responseCode = "400", description = "Booking already canceled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access forbidden"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public void cancel(@PathVariable Long id) {
        bookingService.cancel(id);
    }

}
