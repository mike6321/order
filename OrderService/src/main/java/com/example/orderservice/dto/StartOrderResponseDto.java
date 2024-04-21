package com.example.orderservice.dto;

import com.example.orderservice.entity.ProductOrder;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class StartOrderResponseDto {

    private Long orderId;
    private Map<String, Object> paymentMethod;
    private Map<String, Object> address;

    public static StartOrderResponseDto convert(
            ProductOrder order,
            Map<String, Object> paymentMethod,
            Map<String, Object> address

    ) {
        return StartOrderResponseDto.builder()
                .orderId(order.id)
                .paymentMethod(paymentMethod)
                .address(address)
                .build();
    }

}
