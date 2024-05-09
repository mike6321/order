package com.example.deliveryservice.service;

import blackfriday.protobuf.EdaMessage;
import com.example.deliveryservice.entity.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final DeliveryService deliveryService;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @KafkaListener(topics = "delivery_request")
    public void consumeDeliveryRequest(byte[] message) throws Exception {
        EdaMessage.DeliveryRequestV1 deliveryRequest = EdaMessage.DeliveryRequestV1.parseFrom(message);
        log.info("[delivery_request] consumed: {}", deliveryRequest);

        Delivery delivery = deliveryService.processDelivery(deliveryRequest.getOrderId(), deliveryRequest.getProductName(), deliveryRequest.getProductCount(), deliveryRequest.getAddress());

        EdaMessage.DeliveryStatusUpdateV1 deliveryStatusUpdate = EdaMessage.DeliveryStatusUpdateV1
                .newBuilder()
                .setOrderId(delivery.orderId)
                .setDeliveryId(delivery.id)
                .setDeliveryStatus(delivery.status.toString())
                .build();

        kafkaTemplate.send("delivery_status_update", deliveryStatusUpdate.toByteArray());
    }

}
