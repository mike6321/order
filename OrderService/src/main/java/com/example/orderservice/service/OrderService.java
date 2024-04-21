package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.ProductOrder;
import com.example.orderservice.feign.CatalogClient;
import com.example.orderservice.feign.DeliveryClient;
import com.example.orderservice.feign.PaymentClient;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.orderservice.dto.StartOrderResponseDto.convert;
import static com.example.orderservice.enums.OrderStatus.DELIVERY_REQUESTED;
import static com.example.orderservice.enums.OrderStatus.INITIATED;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final CatalogClient catalogClient;

    public StartOrderResponseDto startOrder(Long userId, Long productId, Long count) {
        // 1. 상품 정보 조회
        var product = catalogClient.getProduct(productId);

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
        var payment = payment(paymentMethodId, order, product);

        // 3. 배송 요청
        var delivery = delivery(addressId, order, product);

        // 4. 상품 재고 감소
        stock(order);

        // 5. 주문 정보 업데이트
        return updateOrderInfo(order, payment, delivery);
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
                null
        );
        return orderRepository.save(order);
    }

    private ProductOrder updateOrderInfo(ProductOrder order, Map<String, Object> payment, Map<String, Object> delivery) {
        order.paymentId = Long.parseLong(payment.get("id").toString());
        order.deliveryId = Long.parseLong(delivery.get("id").toString());
        order.orderStatus = DELIVERY_REQUESTED;

        return orderRepository.save(order);
    }

    private void stock(ProductOrder order) {
        var decreaseStockCountDto = new DecreaseStockCountDto();
        decreaseStockCountDto.decreaseCount = order.count;

        catalogClient.decreaseStockCount(order.productId, decreaseStockCountDto);
    }

    private Map<String, Object> delivery(Long addressId, ProductOrder order, Map<String, Object> product) {
        var address = deliveryClient.getAddress(addressId);

        var processDeliveryDto = new ProcessDeliveryDto();
        processDeliveryDto.orderId = order.id;
        processDeliveryDto.productName = product.get("name").toString();
        processDeliveryDto.productCount = order.count;
        processDeliveryDto.address = address.get("address").toString();

        return deliveryClient.processDelivery(processDeliveryDto);
    }

    private Map<String, Object> payment(Long paymentMethodId, ProductOrder order, Map<String, Object> product) {
        var processPaymentDto = new ProcessPaymentDto();
        processPaymentDto.orderId = order.id;
        processPaymentDto.userId = order.userId;
        processPaymentDto.amountKRW = Long.parseLong(product.get("price").toString()) * order.count;
        processPaymentDto.paymentMethodId = paymentMethodId;

        return paymentClient.processPayment(processPaymentDto);
    }


}
