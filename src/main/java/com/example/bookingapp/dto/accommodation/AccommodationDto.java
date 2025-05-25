package com.example.bookingapp.dto.accommodation;

import com.example.bookingapp.model.Accommodation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AccommodationDto {
    private Long id;

    @NotNull(message = "Housing type is required")
    private Accommodation.PropertyType propertyType;

    @NotNull(message = "Address is required")
    private AddressDto location;

    @NotBlank(message = "Housing size is required")
    private String size;

    private List<String> amenities;

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "0.01", message = "The price per day must be positive.")
    private BigDecimal pricePerDay;

    @NotNull(message = "Accessibility is a must")
    @Min(value = 0, message = "Accessibility should be integral")
    private Integer availability;
}
