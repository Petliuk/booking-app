package com.example.bookingapp.repository.booking;

import com.example.bookingapp.dto.booking.BookingSearchParametersDto;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.SpecificationBuilder;
import com.example.bookingapp.repository.SpecificationProviderManager;
import com.example.bookingapp.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSpecificationBuilder implements SpecificationBuilder<Booking,
        BookingSearchParametersDto> {

    private final SpecificationProviderManager<Booking> specificationProviderManager;

    @Override
    public Specification<Booking> build(BookingSearchParametersDto params) {
        Specification<Booking> spec = Specification.where(null);

        if (params.getUserId() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.USER_ID)
                    .getSpecification(new String[]{params.getUserId().toString()}));
        }
        if (params.getStatus() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.STATUS)
                    .getSpecification(new String[]{params.getStatus()}));
        }
        return spec;
    }
}
