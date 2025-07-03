package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentCreationDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.mapper.PaymentMapper;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.model.Payment;
import com.example.bookingapp.model.Role;
import com.example.bookingapp.repository.booking.BookingRepository;
import com.example.bookingapp.repository.payment.PaymentRepository;
import com.example.bookingapp.repository.user.UserRepository;
import com.example.bookingapp.service.NotificationService;
import com.example.bookingapp.service.PaymentService;
import com.example.bookingapp.service.StripeService;
import com.example.bookingapp.util.Constants;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;
    private final StripeService stripeService;

    @Override
    @Transactional
    public PaymentDto create(CreatePaymentDto dto) {
        Booking booking = getBookingById(dto.getBookingId());
        validateBookingForPayment(booking);
        BigDecimal amountToPay = calculatePaymentAmount(booking);
        Session stripeSession = stripeService.createStripeSession(booking, amountToPay);
        PaymentCreationDto paymentCreationDto = new PaymentCreationDto();
        paymentCreationDto.setBooking(booking);
        paymentCreationDto.setSessionUrl(stripeSession.getUrl());
        paymentCreationDto.setSessionId(stripeSession.getId());
        paymentCreationDto.setAmountToPay(amountToPay);
        Payment payment = paymentMapper.toEntity(paymentCreationDto);
        Payment savedPayment = paymentRepository.save(payment);
        sendPaymentCreationNotification(booking, amountToPay);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public List<PaymentDto> findByUserId(Long userId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getBooking().getUser().getId().equals(userId))
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> findByCurrentUser() {
        Long userId = getCurrentUserId();
        return findByUserId(userId);
    }

    @Override
    public List<PaymentDto> findAll(Long userId) {
        String email = getCurrentUserEmail();
        boolean isManager = isUserManager(email);
        if (!isManager && userId != null) {
            throw new InvalidRequestException("Only managers can view payments of other users");
        }
        return userId != null ? findByUserId(userId) : getAllPayments();
    }

    @Override
    @Transactional
    public PaymentDto handleSuccess(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        try {
            Session session = Session.retrieve(sessionId);
            validateSessionPaymentStatus(session, sessionId);
            updatePaymentAndBookingStatus(payment);
            sendPaymentSuccessNotification(payment);
            return paymentMapper.toDto(payment);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to verify Stripe session: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentDto handleCancel(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        sendPaymentCancellationNotification(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public List<Payment> findExpiredPayments() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == Payment.PaymentStatus.PENDING)
                .filter(this::isPaymentExpired)
                .toList();
    }

    @Override
    public void checkPendingPayments(Long userId) {
        Long pendingCount = paymentRepository.countByStatusAndBookingUserId(
                Payment.PaymentStatus.PENDING, userId);
        if (pendingCount > Constants.NO_PENDING_PAYMENTS) {
            throw new InvalidRequestException("Cannot create new booking: pending payments exist");
        }
    }

    @Override
    public Optional<Payment> findBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId);
    }

    @Override
    @Transactional
    public void markPaymentAsExpired(Payment payment) {
        payment.setStatus(Payment.PaymentStatus.EXPIRED);
        payment.getBooking().setStatus(Booking.BookingStatus.CANCELED);
        paymentRepository.save(payment);
        sendPaymentCancellationNotification(payment);
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID "
                        + id + " not found"));
    }

    private Payment getPaymentBySessionId(String sessionId) {
        return findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment with sessionId "
                        + sessionId + " not found"));
    }

    private void validateBookingForPayment(Booking booking) {
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new InvalidRequestException("Booking must be in PENDING status for payment");
        }
        String currentUserEmail = getCurrentUserEmail();
        if (!booking.getUser().getEmail().equals(currentUserEmail)) {
            throw new InvalidRequestException("You can only pay for your own bookings");
        }
    }

    private BigDecimal calculatePaymentAmount(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return booking.getAccommodation().getPricePerDay().multiply(BigDecimal.valueOf(days));
    }

    private void sendPaymentCreationNotification(Booking booking, BigDecimal amountToPay) {
        String message = String.format(Constants.PAYMENT_CREATED_MESSAGE,
                booking.getId(),
                amountToPay);
        notificationService.sendNotification(message);
    }

    private void sendPaymentSuccessNotification(Payment payment) {
        String message = String.format(Constants.PAYMENT_SUCCESS_MESSAGE,
                payment.getId(), payment.getBooking().getId(), payment.getAmountToPay());
        notificationService.sendNotification(message);
    }

    private void sendPaymentCancellationNotification(Payment payment) {
        String message = String.format(Constants.PAYMENT_CANCELED_MESSAGE,
                payment.getId(), payment.getBooking().getId());
        notificationService.sendNotification(message);
    }

    private Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(com.example.bookingapp.model.User::getId)
                .orElseThrow(() -> new EntityNotFoundException("User with email "
                        + email + " not found"));
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isUserManager(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == Role.RoleName.MANAGER))
                .orElse(false);
    }

    private List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private void validateSessionPaymentStatus(Session session, String sessionId) {
        if (!Constants.PAID_STATUS.equals(session.getPaymentStatus())) {
            throw new InvalidRequestException("Payment session not paid");
        }
    }

    private void updatePaymentAndBookingStatus(Payment payment) {
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.getBooking().setStatus(Booking.BookingStatus.CONFIRMED);
        paymentRepository.save(payment);
    }

    private boolean isPaymentExpired(Payment payment) {
        try {
            Session session = Session.retrieve(payment.getSessionId());
            return session.getExpiresAt()
                    < System.currentTimeMillis() / Constants.MILLISECONDS_TO_SECONDS_DIVIDER;
        } catch (StripeException e) {
            throw new RuntimeException("Failed to check Stripe session: " + e.getMessage());
        }
    }
}
