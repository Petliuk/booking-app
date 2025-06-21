package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.exception.EntityNotFoundException;
import com.example.bookingapp.exception.InvalidRequestException;
import com.example.bookingapp.mapper.AccommodationMapper;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Booking;
import com.example.bookingapp.repository.accommodation.AccommodationRepository;
import com.example.bookingapp.repository.accommodation.AccommodationSpecificationBuilder;
import com.example.bookingapp.repository.booking.BookingRepository;
import com.example.bookingapp.service.AccommodationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final BookingRepository bookingRepository;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;

    @Override
    @Transactional
    public AccommodationDto create(@Valid CreateAccommodationRequestDto dto) {
        validateAccommodationDto(dto);
        Accommodation accommodation = accommodationMapper.toEntity(dto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .map(accommodationMapper::toDto);
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
                .orElseThrow(() -> new EntityNotFoundException("Housing with ID "
                        + id + "not found"));
        if (bookingRepository.existsByAccommodationIdAndStatusNot(id,
                Booking.BookingStatus.CANCELED)) {
            throw new InvalidRequestException("Unable to delete a listing with active bookings");
        }
        accommodationRepository.deleteById(id);
    }

    private void validateAccommodationDto(Object dto) {
        if (dto == null) {
            throw new InvalidRequestException("DTO of housing cannot be null");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("ID must be a positive number");
        }
    }

    @Override
    public List<AccommodationDto> search(AccommodationSearchParametersDto params) {
        Specification<Accommodation> accommodationSpecification
                = accommodationSpecificationBuilder.build(params);
        return accommodationRepository.findAll(accommodationSpecification)
                .stream()
                .map(accommodationMapper::toDto)
                .toList();
    }
}
