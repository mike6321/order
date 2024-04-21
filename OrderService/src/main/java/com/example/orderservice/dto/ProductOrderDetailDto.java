package com.example.orderservice.dto;


import com.example.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductOrderDetailDto {

    public Long id;
    public Long userId;
    public Long productId;
    public Long paymentId;
    public Long deliveryId;
    public OrderStatus orderStatus;

    public String paymentStatus;
    public String deliveryStatus;

}
