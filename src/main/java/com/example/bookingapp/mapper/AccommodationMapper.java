package com.example.bookingapp.mapper;

import com.example.bookingapp.config.MapperConfig;
import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AddressDto;
import com.example.bookingapp.model.Accommodation;
import com.example.bookingapp.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    Address toEntity(AddressDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", source = "location")
    Accommodation toEntity(AccommodationDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "location", source = "location")
    AccommodationDto toDto(Accommodation accommodation);

}
