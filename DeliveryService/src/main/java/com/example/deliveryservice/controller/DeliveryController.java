package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.ProcessDeliveryDto;
import com.example.deliveryservice.dto.RegisterAddressDto;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.UserAddress;
import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/delivery/")
@RequiredArgsConstructor
@RestController
public class DeliveryController {
    
    private final DeliveryService deliveryService;

    @PostMapping("addresses")
    public UserAddress registerAddress(@RequestBody RegisterAddressDto dto) {
        return deliveryService.addUserAddress(
                dto.userId,
                dto.address,
                dto.alias
        );
    }

    @Deprecated
    @PostMapping("process-delivery")
    public Delivery processDelivery(@RequestBody ProcessDeliveryDto dto) {
        return deliveryService.processDelivery(
                dto.orderId,
                dto.productName,
                dto.productCount,
                dto.address
        );
    }

    @GetMapping("deliveries/{deliveryId}")
    public Delivery getDelivery(@PathVariable Long deliveryId) {
        return deliveryService.getDelivery(deliveryId);
    }

    @GetMapping("address/{addressId}")
    public UserAddress getAddress(@PathVariable Long addressId) throws Exception {
        return deliveryService.getAddress(addressId);
    }

    @GetMapping("users/{userId}/first-address")
    public UserAddress getUserAddress(@PathVariable Long userId) throws Exception {
        return deliveryService.getUserAddress(userId);
    }

}
