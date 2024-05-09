package com.example.deliveryservice.dg;

import blackfriday.protobuf.EdaMessage;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.enums.DeliveryStatus;
import com.example.deliveryservice.repositroy.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryStatusUpdater {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Scheduled(fixedDelay = 10000)
    public void deliveryStatusUpdate() {
        System.out.println("----------- delivery status update ------------");

        var inDeliveryList = deliveryRepository.findAllByStatus(DeliveryStatus.IN_DELIVERY);
        inDeliveryList.forEach(delivery -> {
            delivery.status = DeliveryStatus.COMPLETED;
            deliveryRepository.save(delivery);

            publishStatusChange(delivery);
        });

        var requestedList = deliveryRepository.findAllByStatus(DeliveryStatus.REQUESTED);
        requestedList.forEach(delivery -> {
            delivery.status = DeliveryStatus.IN_DELIVERY;
            deliveryRepository.save(delivery);

            publishStatusChange(delivery);
        });
    }

    private void publishStatusChange(Delivery delivery) {
        EdaMessage.DeliveryStatusUpdateV1 deliveryStatusUpdate = EdaMessage.DeliveryStatusUpdateV1
                .newBuilder()
                .setOrderId(delivery.orderId)
                .setDeliveryId(delivery.id)
                .setDeliveryStatus(delivery.status.toString())
                .build();

        kafkaTemplate.send("delivery_status_update", deliveryStatusUpdate.toByteArray());
    }

}
