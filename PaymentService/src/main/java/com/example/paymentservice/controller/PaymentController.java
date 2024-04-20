package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentMethodDto;
import com.example.paymentservice.dto.ProcessPaymentDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentMethod;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/payment/")
@RequiredArgsConstructor
@RestController
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping("methods")
    public PaymentMethod registerPaymentMethod(@RequestBody PaymentMethodDto dto) {
        return paymentService.registerPaymentMethod(
              dto.userId,
              dto.paymentMethodType,
              dto.creditCardNumber
        );
    }

    @PostMapping("process-payment")
    public Payment processPayment(@RequestBody ProcessPaymentDto dto) throws Exception {
        return paymentService.processPayment(
                dto.userId,
                dto.orderId,
                dto.amountKRW,
                dto.paymentMethodId
        );
    }

    @GetMapping("users/{userId}/first-method")
    public PaymentMethod getPaymentMethod(@PathVariable Long userId) {
        return paymentService.getPaymentMethodByUser(userId);
    }
    @GetMapping("payments/{paymentId}")
    public Payment getPayment(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId);
    }

}
