package com.example.bookingapp.controller;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "API для керування платежами")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a payment session",
            description = "Creates a new Stripe payment session for a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment session created"),
            @ApiResponse(responseCode = "400", description = "Invalid booking ID or status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public PaymentDto create(@RequestBody @Valid CreatePaymentDto dto) {
        return paymentService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "List payments",
            description = "Returns a list of payments,"
                    + " optionally filtered by user ID (for managers)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of payments"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<PaymentDto> list(@RequestParam(required = false) Long userId) {
        return paymentService.findAll(userId);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user's payments",
            description = "Returns a list of payments for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of payments"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public List<PaymentDto> getMyPayments() {
        return paymentService.findByCurrentUser();
    }

    @GetMapping("/success")
    @Operation(summary = "Handle successful payment",
            description = "Processes a successful Stripe payment session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment processed"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public PaymentDto handleSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @Operation(summary = "Handle canceled payment",
            description = "Processes a canceled Stripe payment session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment cancellation processed"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public PaymentDto handleCancel(@RequestParam("session_id") String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
