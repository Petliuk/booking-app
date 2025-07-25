package com.example.bookingapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Housing type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Embedded
    private Address location;

    @NotBlank(message = "Housing size is required")
    @Column(nullable = false)
    private String size;

    @ElementCollection
    @CollectionTable(name = "accommodation_amenities")
    private List<String> amenities;

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "0.01", message = "The price per day must be positive.")
    @Column(name = "amount_to_pay", nullable = false)
    private BigDecimal pricePerDay;

    @NotNull(message = "Accessibility is a must")
    @Min(value = 0, message = "Accessibility should be integral")
    @Column(nullable = false)
    private Integer availability;

    public enum PropertyType {
        HOUSE, APARTMENT, CONDO, VACATION_HOME
    }
}
