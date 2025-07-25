package com.example.bookingapp.utils;

import com.example.bookingapp.dto.booking.BookingResponseDto;
import com.example.bookingapp.dto.booking.CreateBookingDto;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Address;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingUtils implements CommonTestConstants {
    public static final String BASE_URL = "/bookings";
    public static final String MY_BOOKINGS_ENDPOINT = "/my";
    public static final Long VALID_BOOKING_ID = 1L;
    public static final Long VALID_ACCOMMODATION_ID = 1L;
    public static final Long VALID_USER_ID = 1L;
    public static final String ADD_BASE_TEST_DATA_SQL = "classpath:database/common/add-base-test-data.sql";
    public static final String CLEAR_BASE_TEST_DATA_SQL = "classpath:database/common/clear-base-test-data.sql";
    public static final String ADD_BOOKING_TEST_DATA_SQL = "classpath:database/booking/add-booking-test-data.sql";
    public static final String CLEAR_BOOKING_TEST_DATA_SQL = "classpath:database/booking/clear-booking-test-data.sql";
    public static final String ADD_ACCOMMODATION_TEST_DATA_SQL = "classpath:database/accommodation/add-accommodation-test-data.sql";
    public static final String CLEAR_ACCOMMODATION_TEST_DATA_SQL = "classpath:database/accommodation/clear-accommodation-test-data.sql";
    public static final Booking.BookingStatus PENDING_STATUS = Booking.BookingStatus.PENDING;
    public static final Booking.BookingStatus CANCELED_STATUS = Booking.BookingStatus.CANCELED;
    public static final String PENDING_STATUS_STRING = "PENDING";
    public static final LocalDate TEST_CHECK_IN_DATE = LocalDate.now().plusDays(1);
    public static final LocalDate TEST_CHECK_OUT_DATE = LocalDate.now().plusDays(5);
    public static final String MANAGER_EMAIL = "manager@example.com";
    public static final Accommodation.PropertyType HOUSE_PROPERTY_TYPE = Accommodation.PropertyType.HOUSE;
    public static final String TEST_CITY = "Lviv";
    public static final BigDecimal TEST_PRICE = new BigDecimal("100.00");
    public static final int TEST_AVAILABILITY = 3;

    public static CreateBookingDto createBookingRequestDto() {
        return createBookingRequestDto(
                TEST_CHECK_IN_DATE,
                TEST_CHECK_OUT_DATE,
                VALID_ACCOMMODATION_ID
        );
    }

    public static CreateBookingDto createBookingRequestDto(LocalDate checkInDate, LocalDate checkOutDate, Long accommodationId) {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setCheckInDate(checkInDate);
        dto.setCheckOutDate(checkOutDate);
        dto.setAccommodationId(accommodationId);
        return dto;
    }

    public static CreateBookingDto createInvalidBookingRequestDto() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setCheckInDate(LocalDate.of(2024, 1, 1));
        dto.setCheckOutDate(LocalDate.of(2024, 1, 2));
        dto.setAccommodationId(VALID_ACCOMMODATION_ID);
        return dto;
    }

    public static BookingResponseDto createBookingResponseDto() {
        return createBookingResponseDto(
                VALID_BOOKING_ID,
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 7, 15),
                VALID_ACCOMMODATION_ID,
                VALID_USER_ID,
                Booking.BookingStatus.CONFIRMED
        );
    }

    public static BookingResponseDto createBookingResponseDto(Long id, LocalDate checkInDate, LocalDate checkOutDate,
                                                              Long accommodationId, Long userId, Booking.BookingStatus status) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(id);
        dto.setCheckInDate(checkInDate);
        dto.setCheckOutDate(checkOutDate);
        dto.setAccommodationId(accommodationId);
        dto.setUserId(userId);
        dto.setStatus(status);
        return dto;
    }

    public static User createUserEntity() {
        return createUserEntity(VALID_USER_ID, MANAGER_EMAIL);
    }

    public static User createUserEntity(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    public static Accommodation createAccommodationEntity() {
        return createAccommodationEntity(
                VALID_ACCOMMODATION_ID,
                HOUSE_PROPERTY_TYPE,
                TEST_CITY,
                TEST_PRICE,
                TEST_AVAILABILITY
        );
    }

    public static Accommodation createAccommodationEntity(Long id, Accommodation.PropertyType propertyType,
                                                          String city, BigDecimal pricePerDay, Integer availability) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setPropertyType(propertyType);
        Address address = new Address();
        address.setCity(city);
        accommodation.setLocation(address);
        accommodation.setPricePerDay(pricePerDay);
        accommodation.setAvailability(availability);
        return accommodation;
    }

    public static Booking createBookingEntity() {
        return createBookingEntity(
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 5),
                createAccommodationEntity(),
                createUserEntity(),
                PENDING_STATUS
        );
    }

    public static Booking createBookingEntity(LocalDate checkInDate, LocalDate checkOutDate,
                                              Accommodation accommodation, User user, Booking.BookingStatus status) {
        Booking booking = new Booking();
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(status);
        return booking;
    }
}
