package com.example.bookingapp.dto.user;

import com.example.bookingapp.model.Role;
import lombok.Data;

@Data
public class RoleUpdateDto {
    private Role.RoleName roleName;
}
