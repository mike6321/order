package com.example.paymentservice.pg;

public interface CreditCardPaymentAdapter {

    Long processCreditCardPayment(Long amountKRW, String creditCardNumber);

}
