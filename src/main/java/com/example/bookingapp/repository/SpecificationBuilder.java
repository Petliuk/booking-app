package com.example.bookingapp.repository;

import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(AccommodationSearchParametersDto bookSearchParametersDto);
}
