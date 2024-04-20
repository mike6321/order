package com.example.paymentservice.dto;


import com.example.paymentservice.entity.enums.PaymentMethodType;

public class PaymentMethodDto {

    public Long userId;
    public PaymentMethodType paymentMethodType;
    public String creditCardNumber;

}
