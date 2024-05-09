package com.example.orderservice.service;

import blackfriday.protobuf.EdaMessage;
import com.example.orderservice.dto.ProductOrderDetailDto;
import com.example.orderservice.dto.StartOrderResponseDto;
import com.example.orderservice.entity.ProductOrder;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.feign.CatalogClient;
import com.example.orderservice.feign.DeliveryClient;
import com.example.orderservice.feign.PaymentClient;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.orderservice.dto.StartOrderResponseDto.convert;
import static com.example.orderservice.enums.OrderStatus.INITIATED;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final CatalogClient catalogClient;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public StartOrderResponseDto startOrder(Long userId, Long productId, Long count) {
        // 1. 상품 정보 조회
        catalogClient.getProduct(productId);

        // 2. 결제 수단 정보 조회
        var paymentMethod = paymentClient.getPaymentMethod(userId);

        // 3. 배송지 정보 조회
        var address = deliveryClient.getUserAddress(userId);

        // 4. 주문 정보 생성
        ProductOrder order = createOrderInfo(userId, productId, count);

        return convert(order, paymentMethod, address);
    }

    public ProductOrder finishOrder(Long orderId, Long paymentMethodId, Long addressId) {
        var order = orderRepository.findById(orderId).orElseThrow();
        // 1. 상품 정보 조회
        var product = catalogClient.getProduct(order.productId);

        // 2. 결제
        payment(paymentMethodId, order, product);

        // 3. 주문 정보 업데이트
        return updateOrderInfo(order, addressId);
    }

    public List<ProductOrder> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public ProductOrderDetailDto getOrderDetail(Long orderId) {
        var order = orderRepository.findById(orderId).orElseThrow();

        var paymentRes = paymentClient.getPayment(order.paymentId);
        var deliveryRes = deliveryClient.getDelivery(order.deliveryId);

        return new ProductOrderDetailDto(
                order.id,
                order.userId,
                order.productId,
                order.paymentId,
                order.deliveryId,
                order.orderStatus,
                paymentRes.get("paymentStatus").toString(),
                deliveryRes.get("status").toString()
        );
    }

    private ProductOrder createOrderInfo(Long userId, Long productId, Long count) {
        var order = new ProductOrder(
                userId,
                productId,
                count,
                INITIATED,
                null,
                null,
                null
        );
        return orderRepository.save(order);
    }

    private ProductOrder updateOrderInfo(ProductOrder order, Long addressId) {
        Map<String, Object> address = deliveryClient.getAddress(addressId);

        order.orderStatus = OrderStatus.PAYMENT_REQUESTED;
        order.deliveryAddress = address.get("address").toString();
        return orderRepository.save(order);
    }

    private void payment(Long paymentMethodId, ProductOrder order, Map<String, Object> product) {
        EdaMessage.PaymentRequestV1 paymentRequest = EdaMessage.PaymentRequestV1
                .newBuilder()
                .setOrderId(order.id)
                .setUserId(order.userId)
                .setAmountKRW(Long.parseLong(product.get("price").toString()) * order.count)
                .setPaymentMethodId(paymentMethodId)
                .build();

        kafkaTemplate.send("payment_request", paymentRequest.toByteArray());
    }


}
