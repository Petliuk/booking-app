package com.example.bookingapp.controller;

import com.example.bookingapp.dto.accommodation.AccommodationDto;
import com.example.bookingapp.dto.accommodation.AccommodationSearchParametersDto;
import com.example.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.example.bookingapp.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public AccommodationDto create(@RequestBody @Valid CreateAccommodationRequestDto dto) {
        return accommodationService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Get all accommodations",
            description = "Returns a list of all available homes")
    public Page<AccommodationDto> getAllAccommodations(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get housing by ID",
            description = "Returns the details of the accommodation by its ID")
    public AccommodationDto getById(@PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Renovate your home",
            description = "Updates property details (for managers only)")
    public AccommodationDto update(@PathVariable Long id,
                                   @RequestBody @Valid AccommodationDto dto) {
        return accommodationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Delete accommodation",
            description = "Deletes a home (for managers only)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accommodationService.delete(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search accommodation",
            description = "Search for accommodation based on specific parameters")
    @GetMapping("/search")
    public List<AccommodationDto> search(
            @ModelAttribute @Valid AccommodationSearchParametersDto params) {
        return accommodationService.search(params);
    }
}
