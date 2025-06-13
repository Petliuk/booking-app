package com.example.bookingapp.dto.accommodation;

import com.example.bookingapp.model.Accommodation.PropertyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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

    @DecimalMin(value = "0.01", message =
            "The minimum price per day must be positive")
    private BigDecimal minPricePerDay;

    @DecimalMin(value = "0.01", message =
            "The maximum price per day must be positive")
    private BigDecimal maxPricePerDay;

    @Min(value = 0, message =
            "Minimum accessibility should be essential")
    private Integer minAvailability;
}
