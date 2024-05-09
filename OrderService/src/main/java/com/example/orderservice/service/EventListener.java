package com.example.orderservice.service;

import blackfriday.protobuf.EdaMessage;
import com.example.orderservice.dto.DecreaseStockCountDto;
import com.example.orderservice.entity.ProductOrder;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.feign.CatalogClient;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    @KafkaListener(topics = "payment_result")
    public void consumePaymentResult(byte[] message) throws Exception {
        EdaMessage.PaymentResultV1 paymentResult = EdaMessage.PaymentResultV1.parseFrom(message);
        log.info("[payment_result] consumed: {}", paymentResult);

        ProductOrder order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
        order.paymentId = paymentResult.getPaymentId();
        order.orderStatus = OrderStatus.DELIVERY_REQUESTED;
        orderRepository.save(order);

        deliveryRequest(order);
    }

    @KafkaListener(topics = "delivery_status_update")
    public void consumeDeliveryStatusUpdate(byte[] message) throws Exception {
        EdaMessage.DeliveryStatusUpdateV1 deliveryStatusUpdate = EdaMessage.DeliveryStatusUpdateV1.parseFrom(message);
        log.info("[delivery_status_update] consumed: {}", deliveryStatusUpdate);

        String deliveryStatus = deliveryStatusUpdate.getDeliveryStatus();
        if (deliveryStatus.equals("REQUESTED")) {
            ProductOrder order = orderRepository.findById(deliveryStatusUpdate.getOrderId()).orElseThrow();
            decreaseStock(order);
        }
    }

    private void deliveryRequest(ProductOrder order) {
        Map<String, Object> product = catalogClient.getProduct(order.productId);

        EdaMessage.DeliveryRequestV1 deliveryRequest = EdaMessage.DeliveryRequestV1.newBuilder()
                .setOrderId(order.id)
                .setProductName(product.get("name").toString())
                .setProductCount(order.count)
                .setAddress(order.deliveryAddress)
                .build();

        kafkaTemplate.send("delivery_request", deliveryRequest.toByteArray());
    }

    private void decreaseStock(ProductOrder order) {
        var decreaseStockCountDto = new DecreaseStockCountDto();
        decreaseStockCountDto.decreaseCount = order.count;
        catalogClient.decreaseStockCount(order.productId, decreaseStockCountDto);
    }

}
