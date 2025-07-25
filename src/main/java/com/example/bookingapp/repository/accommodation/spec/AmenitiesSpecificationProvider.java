package com.example.bookingapp.repository.accommodation.spec;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AmenitiesSpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return Constants.AMENITIES;
    }

    @Override
    public Specification<Accommodation> getSpecification(String[] params) {
        return (root,
                query,
                criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[params.length];
            for (int i = 0; i < params.length; i++) {
                predicates[i] = criteriaBuilder.isMember(params[i], root.get(Constants.AMENITIES));
            }
            return criteriaBuilder.and(predicates);
        };
    }
}
