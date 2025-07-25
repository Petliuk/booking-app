package com.example.bookingapp.dto.accommodation;

import com.example.bookingapp.model.Accommodation.PropertyType;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccommodationSearchParametersDto {
    private PropertyType[] propertyTypes;
    private String[] cities;
    private String[] countries;
    private String[] postalCodes;
    private String[] amenities;

    @Positive(message = "The minimum price per day must be positive")
    private BigDecimal minPricePerDay;

    @Positive(message = "The maximum price per day must be positive")
    private BigDecimal maxPricePerDay;

    @Positive(message = "Minimum availability must be positive")
    private Integer minAvailability;
}
