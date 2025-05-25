package com.example.bookingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateDto {
    @NotBlank(message = "Role name must not be blank")
    private String roleName;
}
