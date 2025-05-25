package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.booking.BookingDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.exception.AccessDeniedException;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.mapper.BookingMapper;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.model.Role;
import com.example.bookingapp.model.User;
import com.example.bookingapp.repository.accommodation.AccommodationRepository;
import com.example.bookingapp.repository.booking.BookingRepository;
import com.example.bookingapp.repository.user.UserRepository;
import com.example.bookingapp.service.BookingService;
import com.example.bookingapp.service.NotificationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingDto create(@Valid CreateBookingDto dto) {
        validateCreateBookingDto(dto);

        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new EntityNotFoundException("Accommodation with ID "
                        + dto.getAccommodationId() + " not found"));
        if (accommodation.getAvailability() <= 0) {
            throw new InvalidRequestException("Accommodation is not available");
        }

        if (bookingRepository.existsByAccommodationIdAndStatusNotAndDateOverlap(
                dto.getAccommodationId(),
                Booking.BookingStatus.CANCELED,
                dto.getCheckInDate(),
                dto.getCheckOutDate())) {
            throw new InvalidRequestException("Accommodation is already booked for these dates");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new EntityNotFoundException("User with email " + email + " not found"));
        Booking booking = bookingMapper.toEntity(dto);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        accommodation.setAvailability(accommodation.getAvailability() - 1);
        accommodationRepository.save(accommodation);
        Booking savedBooking = bookingRepository.save(booking);
        notificationService.sendNotification(
                String.format("New booking #%d created for %s",
                        savedBooking.getId(),
                        accommodation.getLocation().getCity()));
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingDto> findByCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + email + " not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAll(Long userId, String status) {
        List<Booking> bookings;
        if (userId != null && status != null) {
            try {
                Booking.BookingStatus bookingStatus
                        = Booking.BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByUserId(userId).stream()
                        .filter(b -> b.getStatus() == bookingStatus)
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid status: " + status);
            }
        } else if (userId != null) {
            bookings = bookingRepository.findByUserId(userId);
        } else if (status != null) {
            try {
                Booking.BookingStatus bookingStatus
                        = Booking.BookingStatus.valueOf(status.toUpperCase());
                bookings = bookingRepository.findByStatus(bookingStatus);
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid status: " + status);
            }
        } else {
            bookings = bookingRepository.findAll();
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto findById(Long id) {
        validateId(id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Booking with ID "
                        + id + " not found"));
        validateUserAccess(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long id, @Valid BookingDto dto) {
        validateId(id);
        validateBookingDto(dto);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Booking with ID " + id + " not found"));
        validateUserAccess(booking);
        if (bookingRepository.existsByAccommodationIdAndStatusNotAndDateOverlap(
                dto.getAccommodationId(),
                Booking.BookingStatus.CANCELED,
                dto.getCheckInDate(),
                dto.getCheckOutDate())) {
            throw new InvalidRequestException("Accommodation is already booked for these dates");
        }
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setStatus(dto.getStatus());
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        validateId(id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Booking with ID " + id + " not found"));
        validateUserAccess(booking);
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new InvalidRequestException("Booking with ID " + id + " is already canceled");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        Accommodation accommodation = booking.getAccommodation();
        accommodation.setAvailability(accommodation.getAvailability() + 1);
        accommodationRepository.save(accommodation);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                String.format("Booking #%d has been canceled for %s",
                        id,
                        accommodation.getLocation().getCity()));
    }

    @Override
    public List<Booking> findExpiredBookings() {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELED
                        && b.getCheckOutDate().isBefore(LocalDate.now().plusDays(1)))
                .toList();
    }

    @Override
    @Transactional
    public void setExpired(Long id) {
        validateId(id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Booking with ID " + id + " not found"));
        if (booking.getStatus() == Booking.BookingStatus.EXPIRED) {
            throw new InvalidRequestException("Booking with ID " + id + " is already expired");
        }
        booking.setStatus(Booking.BookingStatus.EXPIRED);
        Accommodation accommodation = booking.getAccommodation();
        accommodation.setAvailability(accommodation.getAvailability() + 1);
        accommodationRepository.save(accommodation);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                String.format("Booking #%d has expired for %s",
                        id,
                        accommodation.getLocation().getCity()));
    }

    private void validateCreateBookingDto(CreateBookingDto dto) {
        if (dto.getCheckOutDate().isBefore(dto.getCheckInDate())
                || dto.getCheckOutDate().isEqual(dto.getCheckInDate())) {
            throw new InvalidRequestException("Check-out date must be after check-in date");
        }
    }

    private void validateBookingDto(BookingDto dto) {
        if (dto.getCheckOutDate().isBefore(dto.getCheckInDate())
                || dto.getCheckOutDate().isEqual(dto.getCheckInDate())) {
            throw new InvalidRequestException("Check-out date must be after check-in date");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("ID must be a positive number");
        }
    }

    private void validateUserAccess(Booking booking) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new EntityNotFoundException("User with email " + email + " not found"));
        boolean isManager = user.getRoles()
                .stream()
                .anyMatch(r -> r.getName() == Role.RoleName.MANAGER);
        if (!isManager && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access to this booking is denied");
        }
    }
}
