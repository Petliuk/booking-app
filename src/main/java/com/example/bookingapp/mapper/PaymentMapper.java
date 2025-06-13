package com.example.bookingapp.mapper;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sessionUrl", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "amountToPay", ignore = true)
    Payment toEntity(CreatePaymentDto dto);
}
