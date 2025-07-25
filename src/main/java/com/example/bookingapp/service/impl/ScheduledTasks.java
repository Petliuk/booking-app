package com.example.bookingapp.service.impl;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.model.Payment;
import com.example.bookingapp.service.BookingService;
import com.example.bookingapp.service.NotificationService;
import com.example.bookingapp.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredBookings() {
        List<Booking> expiredBookings = bookingService.findExpiredBookings();
        if (expiredBookings.isEmpty()) {
            notificationService.sendNotification("No overdue reservations today!");
            return;
        }
        for (Booking booking : expiredBookings) {
            bookingService.setExpired(booking.getId());
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void checkExpiredPayments() {
        List<Payment> expiredPayments = paymentService.findExpiredPayments();
        if (expiredPayments.isEmpty()) {
            notificationService.sendNotification("No expired payments today!");
            return;
        }
        expiredPayments.forEach(paymentService::markPaymentAsExpired);
    }
}
