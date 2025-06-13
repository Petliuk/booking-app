package com.example.bookingapp.repository.accommodation.spec;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CitiesSpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return Constants.CITIES;
    }

    public Specification<Accommodation> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get("location").get("city").in((Object[]) params);
    }
}
