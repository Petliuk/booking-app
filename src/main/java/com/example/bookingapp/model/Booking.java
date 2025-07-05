package com.example.bookingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @NotNull(message = "Arrival date is required")
    @Column(nullable = false)
    private LocalDate checkInDate;

    @Future(message = "The departure date must be in the future.")
    @NotNull(message = "Departure date is required.")
    @Column(nullable = false)
    private LocalDate checkOutDate;

    @NotNull(message = "Housing is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @NotNull(message = "User required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELED, EXPIRED
    }
}
