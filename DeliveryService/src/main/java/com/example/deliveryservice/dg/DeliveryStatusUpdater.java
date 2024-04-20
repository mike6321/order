package com.example.deliveryservice.dg;

import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.repositroy.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryStatusUpdater {

    private final DeliveryRepository deliveryRepository;

    @Scheduled(fixedDelay = 10000)
    public void deliveryStatusUpdate() {
        System.out.println("----------- delivery status update ------------");

        var inDeliveryList = deliveryRepository.findAllByStatus(DeliveryStatus.IN_DELIVERY);
        inDeliveryList.forEach(delivery -> {
            delivery.status = DeliveryStatus.COMPLETED;
            deliveryRepository.save(delivery);
        });

        var requestedList = deliveryRepository.findAllByStatus(DeliveryStatus.REQUESTED);
        requestedList.forEach(delivery -> {
            delivery.status = DeliveryStatus.IN_DELIVERY;
            deliveryRepository.save(delivery);
        });
    }

}
