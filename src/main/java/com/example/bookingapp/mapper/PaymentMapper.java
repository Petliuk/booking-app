package com.example.bookingapp.mapper;

import com.example.bookingapp.dto.payment.CreatePaymentDto;
import com.example.bookingapp.dto.payment.PaymentDto;
import com.example.bookingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment toEntity(CreatePaymentDto dto);
}
