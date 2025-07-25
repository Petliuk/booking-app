package com.example.bookingapp.dto.accommodation;

import com.example.bookingapp.model.Accommodation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CreateAccommodationRequestDto {
    @NotNull(message = "Housing type is required")
    private Accommodation.PropertyType propertyType;

    @NotNull(message = "Address is required")
    private AddressDto location;

    @NotBlank(message = "Housing size is required")
    private String size;

    private List<String> amenities;

    @NotNull(message = "Daily price is required")
    @Positive(message = "The price per day must be positive")
    private BigDecimal pricePerDay;

    @NotNull(message = "Accessibility is a must")
    @Positive(message = "Availability must be positive")
    private Integer availability;
}
