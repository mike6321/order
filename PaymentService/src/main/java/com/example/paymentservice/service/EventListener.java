package com.example.paymentservice.service;

import blackfriday.protobuf.EdaMessage;
import com.example.paymentservice.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @KafkaListener(topics = "payment_request")
    public void consumePaymentRequest(byte[] message) throws Exception {
        EdaMessage.PaymentRequestV1 paymentRequest = EdaMessage.PaymentRequestV1.parseFrom(message);
        log.info("[payment_request] consumed: {}", paymentRequest);

        Payment payment = paymentService.processPayment(paymentRequest.getUserId(), paymentRequest.getOrderId(), paymentRequest.getAmountKRW(), paymentRequest.getPaymentMethodId());

        EdaMessage.PaymentResultV1 paymentResult = EdaMessage.PaymentResultV1
                .newBuilder()
                .setOrderId(payment.getOrderId())
                .setPaymentId(payment.getId())
                .setPaymentStatus(payment.getPaymentStatus().toString())
                .build();

        kafkaTemplate.send("payment_result", paymentResult.toByteArray());
    }

}
