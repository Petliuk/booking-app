package com.example.bookingapp.mapper;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentCreationDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment toEntity(CreatePaymentDto dto);

    @Mapping(source = "booking", target = "booking")
    @Mapping(source = "sessionUrl", target = "sessionUrl")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "amountToPay", target = "amountToPay")
    @Mapping(target = "status", constant = "PENDING")
    Payment toEntity(PaymentCreationDto dto);
}
