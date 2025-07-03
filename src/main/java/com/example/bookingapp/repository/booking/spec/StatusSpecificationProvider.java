package com.example.bookingapp.repository.booking.spec;

import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider implements SpecificationProvider<Booking> {

    @Override
    public String getKey() {
        return Constants.STATUS;
    }

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Booking.BookingStatus status = Booking.BookingStatus.valueOf(params[0].toUpperCase());
            return criteriaBuilder.equal(root.get(Constants.STATUS), status);
        };
    }
}
