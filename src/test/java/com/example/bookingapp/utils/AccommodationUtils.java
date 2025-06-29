package com.example.bookingapp.utils;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AddressDto;
import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Address;
import java.math.BigDecimal;
import java.util.List;

public class AccommodationUtils implements CommonTestConstants{
    public static final String BASE_URL = "/accommodations";
    public static final Long VALID_ACCOMMODATION_ID = 1L;
    public static final Long INVALID_ACCOMMODATION_ID = 999L;
    public static final String ADD_BASE_TEST_DATA_SQL = "classpath:database/common/add-base-test-data.sql";
    public static final String CLEAR_BASE_TEST_DATA_SQL = "classpath:database/common/clear-base-test-data.sql";
    public static final String ADD_ACCOMMODATION_TEST_DATA_SQL = "classpath:database/accommodation/add-accommodation-test-data.sql";
    public static final String CLEAR_ACCOMMODATION_TEST_DATA_SQL = "classpath:database/accommodation/clear-accommodation-test-data.sql";
    public static final String MANAGER_USERNAME = "manager";
    public static final String CUSTOMER_USERNAME = "customer";
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_NO_CONTENT = 204;
    public static final String KYIV_CITY = "Kyiv";
    public static final Accommodation.PropertyType APARTMENT_PROPERTY_TYPE = Accommodation.PropertyType.APARTMENT;
    public static final String EXPECTED_CITY = "Kyiv";
    public static final BigDecimal EXPECTED_PRICE = new BigDecimal("50.00");
    public static final Accommodation.PropertyType HOUSE_PROPERTY_TYPE = Accommodation.PropertyType.HOUSE;
    public static final String TEST_STREET = "456 New St";
    public static final String TEST_CITY = "Lviv";
    public static final String TEST_COUNTRY = "Ukraine";
    public static final String TEST_POSTAL_CODE = "79000";
    public static final String TEST_SIZE = "120 sqm";
    public static final List<String> TEST_AMENITIES = List.of("WiFi", "Pool");
    public static final BigDecimal TEST_PRICE = new BigDecimal("150.00");
    public static final int TEST_AVAILABILITY = 3;

    public static CreateAccommodationRequestDto createAccommodationRequestDto() {
        return createAccommodationRequestDto(
                HOUSE_PROPERTY_TYPE,
                "123 Main St",
                KYIV_CITY,
                "Ukraine",
                "01001",
                "100 sqm",
                List.of("WiFi", "Parking"),
                new BigDecimal("100.00"),
                5
        );
    }

    public static CreateAccommodationRequestDto createAccommodationRequestDto(Accommodation.PropertyType propertyType,
                                                                              String street, String city, String country,
                                                                              String postalCode, String size,
                                                                              List<String> amenities, BigDecimal pricePerDay,
                                                                              Integer availability) {
        CreateAccommodationRequestDto dto = new CreateAccommodationRequestDto();
        dto.setPropertyType(propertyType);
        AddressDto address = new AddressDto();
        address.setStreet(street);
        address.setCity(city);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        dto.setLocation(address);
        dto.setSize(size);
        dto.setAmenities(amenities);
        dto.setPricePerDay(pricePerDay);
        dto.setAvailability(availability);
        return dto;
    }

    public static CreateAccommodationRequestDto createInvalidAccommodationRequestDto() {
        CreateAccommodationRequestDto dto = new CreateAccommodationRequestDto();
        dto.setPropertyType(null);
        AddressDto address = new AddressDto();
        address.setStreet("");
        address.setCity("");
        address.setCountry("");
        address.setPostalCode("");
        dto.setLocation(address);
        dto.setSize("");
        dto.setPricePerDay(new BigDecimal("0.00"));
        dto.setAvailability(-1);
        return dto;
    }

    public static AccommodationDto createAccommodationDto() {
        return createAccommodationDto(
                VALID_ACCOMMODATION_ID,
                Accommodation.PropertyType.CONDO,
                TEST_STREET,
                TEST_CITY,
                TEST_COUNTRY,
                TEST_POSTAL_CODE,
                TEST_SIZE,
                TEST_AMENITIES,
                TEST_PRICE,
                TEST_AVAILABILITY
        );
    }

    public static AccommodationDto createAccommodationDto(Long id, Accommodation.PropertyType propertyType,
                                                          String street, String city, String country, String postalCode,
                                                          String size, List<String> amenities, BigDecimal pricePerDay,
                                                          Integer availability) {
        AccommodationDto dto = new AccommodationDto();
        dto.setId(id);
        dto.setPropertyType(propertyType);
        AddressDto address = new AddressDto();
        address.setStreet(street);
        address.setCity(city);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        dto.setLocation(address);
        dto.setSize(size);
        dto.setAmenities(amenities);
        dto.setPricePerDay(pricePerDay);
        dto.setAvailability(availability);
        return dto;
    }

    public static Accommodation createAccommodationEntity() {
        return createAccommodationEntity(
                HOUSE_PROPERTY_TYPE,
                TEST_STREET,
                TEST_CITY,
                TEST_COUNTRY,
                TEST_POSTAL_CODE,
                TEST_SIZE,
                TEST_AMENITIES,
                TEST_PRICE,
                TEST_AVAILABILITY
        );
    }

    public static Accommodation createAccommodationEntity(Accommodation.PropertyType propertyType,
                                                          String street, String city, String country,
                                                          String postalCode, String size,
                                                          List<String> amenities, BigDecimal pricePerDay,
                                                          Integer availability) {
        Accommodation accommodation = new Accommodation();
        accommodation.setPropertyType(propertyType);
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        accommodation.setLocation(address);
        accommodation.setSize(size);
        accommodation.setAmenities(amenities);
        accommodation.setPricePerDay(pricePerDay);
        accommodation.setAvailability(availability);
        return accommodation;
    }
}
