package com.example.bookingapp.repository.accommodation;

import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationBuilder;
import com.example.bookingapp.repository.SpecificationProviderManager;
import com.example.bookingapp.util.Constants;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccommodationSpecificationBuilder implements SpecificationBuilder<Accommodation,
        AccommodationSearchParametersDto> {

    private final SpecificationProviderManager<Accommodation> specificationProviderManager;

    @Override
    public Specification<Accommodation> build(AccommodationSearchParametersDto params) {
        Specification<Accommodation> spec = Specification.where(null);

        if (params.getPropertyTypes() != null && params.getPropertyTypes().length > 0) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.PROPERTY_TYPES)
                    .getSpecification(Arrays.stream(params.getPropertyTypes())
                            .map(Enum::name)
                            .toArray(String[]::new)));
        }
        if (params.getCities() != null && params.getCities().length > 0) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.CITIES)
                    .getSpecification(params.getCities()));
        }
        if (params.getCountries() != null && params.getCountries().length > 0) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.COUNTRIES)
                    .getSpecification(params.getCountries()));
        }
        if (params.getPostalCodes() != null && params.getPostalCodes().length > 0) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.POSTAL_CODES)
                    .getSpecification(params.getPostalCodes()));
        }
        if (params.getAmenities() != null && params.getAmenities().length > 0) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.AMENITIES)
                    .getSpecification(params.getAmenities()));
        }
        if (params.getMinPricePerDay() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.MIN_PRICE_PER_DAY)
                    .getSpecification(new String[]{params.getMinPricePerDay().toString()}));
        }
        if (params.getMaxPricePerDay() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.MAX_PRICE_PER_DAY)
                    .getSpecification(new String[]{params.getMaxPricePerDay().toString()}));
        }
        if (params.getMinAvailability() != null) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider(Constants.MIN_AVAILABILITY)
                    .getSpecification(new String[]{params.getMinAvailability().toString()}));
        }
        return spec;
    }
}
