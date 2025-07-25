package com.example.bookingapp.utils;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.model.Payment;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import static com.example.bookingapp.utils.BookingUtils.VALID_BOOKING_ID;
import static org.mockito.Mockito.*;

public class PaymentUtils implements CommonTestConstants {

    public static final String VALID_SESSION_ID = "sess_123";
    public static final String SESSION_URL = "http://stripe.com/session";
    public static final String PAID_STATUS = "paid";
    public static final String ADD_BASE_TEST_DATA_SQL = "classpath:database/common/add-base-test-data.sql";
    public static final String ADD_BOOKING_TEST_DATA_SQL = "classpath:database/booking/add-booking-test-data.sql";
    public static final String ADD_PAYMENT_TEST_DATA_SQL = "classpath:database/payment/add-payment-test-data.sql";
    public static final String CLEAR_PAYMENT_TEST_DATA_SQL = "classpath:database/payment/clear-payment-test-data.sql";
    public static final String CLEAR_BASE_TEST_DATA_SQL = "classpath:database/common/clear-base-test-data.sql";
    public static final String INVALID_SESSION_ID = "invalid_session";
    public static final Payment.PaymentStatus PENDING_STATUS = Payment.PaymentStatus.PENDING;
    public static final String TEST_SESSION_ID = "sess_456";
    public static final String TEST_SESSION_URL = "http://stripe.com/session2";
    public static final BigDecimal TEST_AMOUNT = new BigDecimal("300.00");

    public static Session mockStripeSession(String sessionId, String url, String paymentStatus) {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);
        when(session.getUrl()).thenReturn(url);
        when(session.getExpiresAt()).thenReturn(System.currentTimeMillis() / 1000 + 3600);
        when(session.getPaymentStatus()).thenReturn(paymentStatus);
        return session;
    }

    public static Payment createPaymentEntity() {
        return createPaymentEntity(
                createBookingEntity(),
                PENDING_STATUS,
                TEST_SESSION_ID,
                TEST_SESSION_URL,
                TEST_AMOUNT
        );
    }

    public static Payment createPaymentEntity(Booking booking, Payment.PaymentStatus status,
                                              String sessionId, String sessionUrl, BigDecimal amountToPay) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(status);
        payment.setSessionId(sessionId);
        payment.setSessionUrl(sessionUrl);
        payment.setAmountToPay(amountToPay);
        return payment;
    }

    public static Booking createBookingEntity() {
        Booking booking = new Booking();
        booking.setId(VALID_BOOKING_ID);
        return booking;
    }
}
