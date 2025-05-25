package com.example.bookingapp.service;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import java.util.List;

public interface AccommodationService {
    AccommodationDto create(AccommodationDto dto);

    List<AccommodationDto> findAll();

    AccommodationDto findById(Long id);

    AccommodationDto update(Long id, AccommodationDto dto);

    void delete(Long id);
}
