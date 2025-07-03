package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.BookingSearchParametersDto;
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
import com.example.bookingapp.repository.booking.BookingSpecificationBuilder;
import com.example.bookingapp.repository.user.UserRepository;
import com.example.bookingapp.service.BookingService;
import com.example.bookingapp.service.NotificationService;
import com.example.bookingapp.service.PaymentService;
import com.example.bookingapp.util.Constants;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    private final PaymentService paymentService;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;

    @Override
    @Transactional
    public BookingResponseDto create(@Valid CreateBookingDto dto) {
        User user = getCurrentUser();
        paymentService.checkPendingPayments(user.getId());

        Accommodation accommodation = getAccommodationById(dto.getAccommodationId());
        validateAccommodationAvailability(accommodation,
                dto.getCheckInDate(),
                dto.getCheckOutDate());
        Booking booking = bookingMapper.toEntity(dto);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        updateAccommodationAvailability(accommodation, Constants.DECREMENT_AVAILABILITY);
        Booking savedBooking = bookingRepository.save(booking);
        notificationService.sendNotification(String.format("New booking #%d created for %s",
                savedBooking.getId(), accommodation.getLocation().getCity()));
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingResponseDto> findByCurrentUser() {
        User user = getCurrentUser();
        return mapToDtoList(bookingRepository.findByUserId(user.getId()));
    }

    @Override
    public List<BookingResponseDto> findAll(BookingSearchParametersDto params) {
        Specification<Booking> spec = bookingSpecificationBuilder.build(params);
        List<Booking> bookings = bookingRepository.findAll(spec);
        return mapToDtoList(bookings);
    }

    @Override
    public BookingResponseDto findById(Long id) {
        validateId(id);
        Booking booking = getBookingById(id);
        validateUserAccess(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto update(Long id, BookingResponseDto dto) {
        validateId(id);
        Booking booking = getBookingById(id);
        validateUserAccess(booking);

        long activeBookings = countActiveBookings(dto.getAccommodationId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate());
        if (booking.getId().equals(id)) {
            activeBookings = activeBookings > Constants.NO_ACTIVE_BOOKINGS
                    ? activeBookings - Constants.INCREMENT_AVAILABILITY
                    : Constants.NO_ACTIVE_BOOKINGS;
        }

        Accommodation accommodation = getAccommodationById(dto.getAccommodationId());
        if (activeBookings >= accommodation.getAvailability()) {
            throw new InvalidRequestException("No available units for these dates");
        }

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setStatus(dto.getStatus());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        validateId(id);
        Booking booking = getBookingById(id);
        validateUserAccess(booking);
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new InvalidRequestException("Booking with ID "
                    + id + " is already canceled");
        }
        finalizeBooking(booking, Booking.BookingStatus.CANCELED, "canceled");
    }

    @Override
    public List<Booking> findExpiredBookings() {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELED
                        && b.getCheckOutDate().isBefore(LocalDate.now()
                        .plusDays(Constants.EXPIRY_DAYS_OFFSET)))
                .toList();
    }

    @Override
    @Transactional
    public void setExpired(Long id) {
        validateId(id);
        Booking booking = getBookingById(id);
        if (booking.getStatus() == Booking.BookingStatus.EXPIRED) {
            throw new InvalidRequestException("Booking with ID "
                    + id + " is already expired");
        }
        finalizeBooking(booking, Booking.BookingStatus.EXPIRED, "expired");
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + email + " not found"));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID "
                        + id + " not found"));
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation with ID "
                        + id + " not found"));
    }

    private void validateId(Long id) {
        if (id == null || id <= Constants.NO_ACTIVE_BOOKINGS) {
            throw new InvalidRequestException("ID must be a positive number");
        }
    }

    private void validateUserAccess(Booking booking) {
        User user = getCurrentUser();
        boolean isManager = user.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.RoleName.MANAGER);
        if (!isManager && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access to this booking is denied");
        }
    }

    private void validateAccommodationAvailability(Accommodation accommodation,
                                                   LocalDate checkInDate,
                                                   LocalDate checkOutDate) {
        if (accommodation.getAvailability() <= Constants.NO_ACTIVE_BOOKINGS) {
            throw new InvalidRequestException("Accommodation is not available");
        }
        long activeBookings = countActiveBookings(accommodation.getId(),
                checkInDate, checkOutDate);
        if (activeBookings >= accommodation.getAvailability()) {
            throw new InvalidRequestException("No available units for these dates");
        }
    }

    private long countActiveBookings(Long accommodationId,
                                     LocalDate checkInDate,
                                     LocalDate checkOutDate) {
        return bookingRepository.countByAccommodationIdAndStatusNotAndDateOverlap(
                accommodationId,
                Booking.BookingStatus.CANCELED,
                checkInDate, checkOutDate);
    }

    private void updateAccommodationAvailability(Accommodation accommodation,
                                                 int delta) {
        accommodation.setAvailability(accommodation.getAvailability() + delta);
        accommodationRepository.save(accommodation);
    }

    private void finalizeBooking(Booking booking, Booking.BookingStatus status, String action) {
        booking.setStatus(status);
        updateAccommodationAvailability(booking.getAccommodation(),
                Constants.INCREMENT_AVAILABILITY);
        Booking savedBooking = bookingRepository.save(booking);
        notificationService.sendNotification(String.format("Booking #%d has been %s for %s",
                savedBooking.getId(), action, booking.getAccommodation().getLocation().getCity()));
    }

    private List<BookingResponseDto> mapToDtoList(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::toDto).toList();
    }
}
