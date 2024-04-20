package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentMethod;
import com.example.paymentservice.entity.enums.PaymentMethodType;
import com.example.paymentservice.entity.enums.PaymentStatus;
import com.example.paymentservice.pg.CreditCardPaymentAdapter;
import com.example.paymentservice.repository.PaymentMethodRepository;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final CreditCardPaymentAdapter creditCardPaymentAdapter;

    public PaymentMethod registerPaymentMethod(
            Long userId,
            PaymentMethodType paymentMethodType,
            String creditCardNumber
    ) {
        var paymentMethod = new PaymentMethod(userId, paymentMethodType, creditCardNumber);
        return paymentMethodRepository.save(paymentMethod);
    }

    public Payment processPayment(
            Long userId,
            Long orderId,
            Long amountKRW,
            Long paymentMethodId
    ) throws Exception {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElseThrow();

        if(paymentMethod.getPaymentMethodType() != PaymentMethodType.CREDIT_CARD) {
            throw new Exception("Unsupported payment method type");
        }

        var refCode = creditCardPaymentAdapter.processCreditCardPayment(amountKRW, paymentMethod.getCreditCardNumber());

        var payment = new Payment(userId,
                orderId,
                amountKRW,
                paymentMethod.getPaymentMethodType(),
                paymentMethod.getCreditCardNumber(),
                PaymentStatus.COMPLETED,
                refCode
        );

        return paymentRepository.save(payment);
    }

    public PaymentMethod getPaymentMethodByUser(Long userId) {
        return paymentMethodRepository.findByUserId(userId).stream().findFirst().orElseThrow();
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }



}
