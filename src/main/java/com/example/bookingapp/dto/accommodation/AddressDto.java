package com.example.bookingapp.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "Вулиця обов’язкова")
    private String street;

    @NotBlank(message = "Місто обов’язкове")
    private String city;

    @NotBlank(message = "Країна обов’язкова")
    private String country;

    @NotBlank(message = "Поштовий код обов’язковий")
    private String postalCode;
}
