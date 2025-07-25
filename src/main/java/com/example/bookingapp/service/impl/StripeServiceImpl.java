package com.example.bookingapp.service.impl;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.util.Constants;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StripeServiceImpl implements com.example.bookingapp.service.StripeService {
    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public Session createStripeSession(Booking booking, BigDecimal amountToPay) {
        String normalizedBaseUrl = normalizeBaseUrl();
        String successUrl = buildStripeUrl(normalizedBaseUrl, Constants.SUCCESS_PATH);
        String cancelUrl = buildStripeUrl(normalizedBaseUrl, Constants.CANCEL_PATH);
        SessionCreateParams params = buildSessionCreateParams(booking,
                amountToPay,
                successUrl,
                cancelUrl);
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session: " + e.getMessage());
        }
    }

    private String normalizeBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL is not configured");
        }
        return baseUrl.startsWith(Constants.HTTP_SCHEME)
                || baseUrl.startsWith(Constants.HTTPS_SCHEME)
                ? baseUrl
                : Constants.HTTP_SCHEME + baseUrl;
    }

    private String buildStripeUrl(String baseUrl, String path) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam(Constants.SESSION_ID, Constants.STRIPE_CHECKOUT_SESSION_ID)
                .build()
                .toUriString();
    }

    private SessionCreateParams buildSessionCreateParams(
            Booking booking, BigDecimal amountToPay, String successUrl, String cancelUrl) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(buildLineItem(booking, amountToPay))
                .build();
    }

    private SessionCreateParams.LineItem buildLineItem(Booking booking,
                                                       BigDecimal amountToPay) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Constants.LINE_ITEM_QUANTITY)
                .setPriceData(buildPriceData(booking, amountToPay))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData buildPriceData(Booking booking,
                                                                  BigDecimal amountToPay) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(Constants.USD_CURRENCY)
                .setUnitAmount(amountToPay.multiply(Constants.CENTS_MULTIPLIER).longValue())
                .setProductData(buildProductData(booking))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData buildProductData(Booking booking) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Booking #" + booking.getId())
                .build();
    }
}
