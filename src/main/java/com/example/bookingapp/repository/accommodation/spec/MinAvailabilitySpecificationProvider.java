package com.example.bookingapp.repository.accommodation.spec;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class MinAvailabilitySpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return Constants.MIN_AVAILABILITY;
    }

    public Specification<Accommodation> getSpecification(String[] params) {
        Integer minAvailability = Integer.parseInt(params[0]);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(Constants.AVAILABILITY),
                        minAvailability);
    }
}
