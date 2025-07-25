package com.example.bookingapp.repository.accommodation.spec;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.util.Constants;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class MaxPricePerDaySpecificationProvider implements SpecificationProvider<Accommodation> {
    @Override
    public String getKey() {
        return Constants.MAX_PRICE_PER_DAY;
    }

    public Specification<Accommodation> getSpecification(String[] params) {
        BigDecimal maxPrice = new BigDecimal(params[0]);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(Constants.PRICE_PER_DAY), maxPrice);
    }
}
