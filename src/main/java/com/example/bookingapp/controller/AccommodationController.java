package com.example.bookingapp.controller;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
@Tag(name = "Accommodation Management", description = "API for housing management")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Create housing",
            description = "Creates new housing (for managers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Housing created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public AccommodationDto create(@RequestBody @Valid AccommodationDto dto) {
        return accommodationService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Get all accommodations",
            description = "Returns a list of all available homes")
    @ApiResponse(responseCode = "200", description = "List of residences")
    public List<AccommodationDto> list() {
        return accommodationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get housing by ID",
            description = "Returns the details of the accommodation by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Housing found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID"),
            @ApiResponse(responseCode = "404", description = "No accommodation found")
    })
    public AccommodationDto getById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Renovate your home",
            description = "Updates property details (for managers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Housing updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No accommodation found")
    })
    public AccommodationDto update(@PathVariable Long id,
                                   @RequestBody @Valid AccommodationDto dto) {
        return accommodationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Delete accommodation",
            description = "Deletes a home (for managers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Housing removed"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or active reservations"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No accommodation found")
    })
    public void delete(@PathVariable Long id) {
        accommodationService.delete(id);
    }
}
