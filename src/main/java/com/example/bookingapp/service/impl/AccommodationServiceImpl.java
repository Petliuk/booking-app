package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.mapper.AccommodationMapper;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.accommodation.AccommodationRepository;
import com.example.bookingapp.repository.booking.BookingRepository;
import com.example.bookingapp.service.AccommodationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public AccommodationDto create(@Valid AccommodationDto dto) {
        validateAccommodationDto(dto);
        Accommodation accommodation = accommodationMapper.toEntity(dto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public List<AccommodationDto> findAll() {
        return accommodationRepository.findAll().stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationDto findById(Long id) {
        validateId(id);
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    @Transactional
    public AccommodationDto update(Long id, @Valid AccommodationDto dto) {
        validateId(id);
        validateAccommodationDto(dto);
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation not found"));
        accommodation.setPropertyType(dto.getPropertyType());
        accommodation.setLocation(accommodationMapper.toEntity(dto.getLocation()));
        accommodation.setSize(dto.getSize());
        accommodation.setAmenities(dto.getAmenities());
        accommodation.setPricePerDay(dto.getPricePerDay());
        accommodation.setAvailability(dto.getAvailability());
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        validateId(id);
        accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Житло з ID "
                        + id + " не знайдено"));
        if (bookingRepository.existsByAccommodationIdAndStatusNot(id,
                Booking.BookingStatus.CANCELED)) {
            throw new InvalidRequestException("Неможливо видалити житло "
                    + "з активними бронюваннями");
        }
        accommodationRepository.deleteById(id);
    }

    private void validateAccommodationDto(AccommodationDto dto) {
        if (dto == null) {
            throw new InvalidRequestException("DTO житла не може бути null");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("ID має бути позитивним числом");
        }
    }
}
