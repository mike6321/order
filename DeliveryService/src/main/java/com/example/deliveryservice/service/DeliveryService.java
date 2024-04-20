package com.example.deliveryservice.service;

import com.example.deliveryservice.dg.DeliveryAdapter;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.UserAddress;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.repositroy.DeliveryRepository;
import com.example.deliveryservice.repositroy.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final UserAddressRepository userAddressRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryAdapter deliveryAdapter;

    public UserAddress addUserAddress(Long userId, String address, String alias) {
        var userAddress = new UserAddress(userId, address, alias);

        return userAddressRepository.save(userAddress);
    }

    public Delivery processDelivery(
            Long orderId,
            String productName,
            Long productCount,
            String address
    ) {
        var referenceCode = deliveryAdapter.processDelivery(productName, productCount, address);
        var delivery = new Delivery(orderId, productName, productCount, address, referenceCode, DeliveryStatus.REQUESTED);

        return deliveryRepository.save(delivery);
    }

    public Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow();
    }

    public UserAddress getAddress(Long addressId) {
        return userAddressRepository.findById(addressId).orElseThrow();
    }

    public UserAddress getUserAddress(Long userId) {
        return userAddressRepository.findByUserId(userId).stream().findFirst().orElseThrow();
    }

}
