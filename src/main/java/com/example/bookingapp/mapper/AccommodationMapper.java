package com.example.bookingapp.mapper;

import com.example.bookingapp.config.MapperConfig;
import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AddressDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    Address toEntity(AddressDto dto);

    Accommodation toEntity(CreateAccommodationRequestDto dto);

    AccommodationDto toDto(Accommodation accommodation);

}
