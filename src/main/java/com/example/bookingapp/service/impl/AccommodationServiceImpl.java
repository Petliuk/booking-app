package com.example.bookingapp.service.impl;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.dto.accommodation.UpdateAccommodationRequestDto;
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
    public AccommodationDto create(CreateAccommodationRequestDto dto) {
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
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accommodation with: "
                        + id + "not found"));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    @Transactional
    public AccommodationDto update(Long id, UpdateAccommodationRequestDto dto) {
        validateAccommodationDto(dto);
        Accommodation existingAccommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Accommodation with: " + id + "not found"));
        Accommodation updatedAccommodation = accommodationMapper.toEntity(dto);
        updatedAccommodation.setId(existingAccommodation.getId());
        return accommodationMapper.toDto(accommodationRepository.save(updatedAccommodation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Housing with ID " + id + "not found"));
        if (bookingRepository.existsByAccommodationIdAndStatusNot(id,
                Booking.BookingStatus.CANCELED)) {
            throw new InvalidRequestException(
                    "Unable to delete accommodation with ID "
                            + id + " because it has active bookings");
        }
        accommodationRepository.deleteById(id);
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

    private void validateAccommodationDto(Object dto) {
        if (dto == null) {
            throw new InvalidRequestException("DTO of housing cannot be null");
        }
    }
}
