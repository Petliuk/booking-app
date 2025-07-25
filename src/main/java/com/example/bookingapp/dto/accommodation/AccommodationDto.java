package com.example.bookingapp.dto.accommodation;

import com.example.bookingapp.model.Accommodation;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AccommodationDto {
    private Long id;
    private Accommodation.PropertyType propertyType;
    private AddressDto location;
    private String size;
    private List<String> amenities;
    private BigDecimal pricePerDay;
    private Integer availability;
}
