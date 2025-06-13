package com.example.bookingapp.util;

import java.math.BigDecimal;

public class Constants {

    public static final String PROPERTY_TYPES = "propertyTypes";
    public static final String CITIES = "cities";
    public static final String COUNTRIES = "countries";
    public static final String POSTAL_CODES = "postalCodes";
    public static final String AMENITIES = "amenities";
    public static final String MIN_PRICE_PER_DAY = "minPricePerDay";
    public static final String MAX_PRICE_PER_DAY = "maxPricePerDay";
    public static final String MIN_AVAILABILITY = "minAvailability";
    public static final String BEARER_FORMAT = "JWT";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String SCHEME_TYPE = "bearer";
    public static final String SECURITY_SCHEME_NAME = "BearerAuth";
    public static final String CANCEL_PATH = "/payments/cancel";
    public static final BigDecimal CENTS_MULTIPLIER = BigDecimal.valueOf(100);
    public static final String HTTP_SCHEME = "http://";
    public static final String HTTPS_SCHEME = "https://";
    public static final long MILLISECONDS_TO_SECONDS_DIVIDER = 1000;
    public static final long NO_PENDING_PAYMENTS = 0;
    public static final String PAID_STATUS = "paid";
    public static final String PAYMENT_CANCELED_MESSAGE =
            "Payment #%d for booking #%d canceled. "
                    + "Please complete payment within 24 hours.";
    public static final String PAYMENT_CREATED_MESSAGE =
            "New payment session created for booking #%d, "
                    + "amount: $%s";
    public static final String PAYMENT_EXPIRED_MESSAGE =
            "Payment #%d for booking #%d has expired";
    public static final String PAYMENT_SUCCESS_MESSAGE =
            "Payment #%d for booking #%d successfully completed, "
                    + "amount: $%s";
    public static final String STRIPE_CHECKOUT_SESSION_ID = "{CHECKOUT_SESSION_ID}";
    public static final String SUCCESS_PATH = "/payments/success";
    public static final String USD_CURRENCY = "usd";
    public static final int DECREMENT_AVAILABILITY = -1;
    public static final long EXPIRY_DAYS_OFFSET = 1;
    public static final int INCREMENT_AVAILABILITY = 1;
    public static final long NO_ACTIVE_BOOKINGS = 0;

    private Constants() {
    }
}
