package com.example.bookingapp.service;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto create(CreateAccommodationRequestDto dto);

    Page<AccommodationDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationDto update(Long id, UpdateAccommodationRequestDto dto);

    void delete(Long id);

    List<AccommodationDto> search(AccommodationSearchParametersDto params);
}
