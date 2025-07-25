package com.example.bookingapp.repository.accommodation;

import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.repository.SpecificationProvider;
import com.example.bookingapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationProviderManager implements
        SpecificationProviderManager<Accommodation> {
    private final List<SpecificationProvider<Accommodation>> accommodationSpecificationProviders;

    @Override
    public SpecificationProvider getSpecificationProvider(String key) {
        return accommodationSpecificationProviders
                .stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find current specification provider or key " + key));
    }
}
