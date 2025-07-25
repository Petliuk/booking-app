package com.example.bookingapp.service;

import com.example.bookingapp.model.Booking;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {
    Session createStripeSession(Booking booking, BigDecimal amountToPay);
}
