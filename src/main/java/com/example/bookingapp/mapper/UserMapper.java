package com.example.bookingapp.mapper;

import com.example.bookingapp.config.MapperConfig;
import com.example.bookingapp.dto.user.UserRegistrationRequestDto;
import com.example.bookingapp.dto.user.UserResponseDto;
import com.example.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto requestDto);

    @Mapping(target = "roles",
            expression = "java(user.getRoles()"
                    + ".stream()"
                    + ".map(role -> role.getName().name())"
                    + ".collect(java.util.stream.Collectors.toSet()))")
    UserResponseDto toDto(User user);
}
