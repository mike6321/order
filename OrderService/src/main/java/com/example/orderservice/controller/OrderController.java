package com.example.orderservice.controller;

import com.example.orderservice.dto.FinishOrderDto;
import com.example.orderservice.dto.ProductOrderDetailDto;
import com.example.orderservice.dto.StartOrderDto;
import com.example.orderservice.dto.StartOrderResponseDto;
import com.example.orderservice.entity.ProductOrder;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/order/")
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("start-order")
    public StartOrderResponseDto startOrder(@RequestBody StartOrderDto dto) throws Exception {
        return orderService.startOrder(dto.userId, dto.productId, dto.count);
    }

    @PostMapping("finish-order")
    public ProductOrder finishOrder(@RequestBody FinishOrderDto dto) throws Exception {
        return orderService.finishOrder(dto.orderId, dto.paymentMethodId, dto.addressId);
    }

    @GetMapping("users/{userId}/orders")
    public List<ProductOrder> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("orders/{orderId}")
    public ProductOrderDetailDto getOrder(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

}
