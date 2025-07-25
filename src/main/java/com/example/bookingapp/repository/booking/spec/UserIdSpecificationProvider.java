package com.example.bookingapp.repository.booking.spec;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserIdSpecificationProvider implements SpecificationProvider<Booking> {

    @Override
    public String getKey() {
        return Constants.USER_ID;
    }

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Long userId = Long.valueOf(params[0]);
            return criteriaBuilder.equal(root.get(Constants.USER).get(Constants.ID), userId);
        };
    }
}
