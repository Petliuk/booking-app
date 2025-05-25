package com.example.bookingapp.service.impl;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.service.BookingService;
import com.example.bookingapp.service.NotificationService;
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

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiredBookings() {
        List<Booking> expiredBookings = bookingService.findExpiredBookings();
        if (expiredBookings.isEmpty()) {
            notificationService.sendNotification("Сьогодні немає прострочених бронювань!");
            return;
        }
        for (Booking booking : expiredBookings) {
            bookingService.setExpired(booking.getId());
        }
    }
}
