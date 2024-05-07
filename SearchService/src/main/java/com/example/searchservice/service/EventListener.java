package com.example.searchservice.service;

import blackfriday.protobuf.EdaMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final SearchService searchService;

    @KafkaListener(topics = "product_tags_added")
    public void consumeTagAdded(byte[] message) throws InvalidProtocolBufferException {
        EdaMessage.ProductTags productTags = EdaMessage.ProductTags.parseFrom(message);
        log.info("[product_tags_added] consumed: {}", productTags);

        searchService.addTagCache(productTags.getProductId(), productTags.getTagsList());
    }

    @KafkaListener(topics = "product_tags_removed")
    public void consumeTagRemoved(byte[] message) throws InvalidProtocolBufferException {
        EdaMessage.ProductTags productTags = EdaMessage.ProductTags.parseFrom(message);
        log.info("[product_tags_removed] consumed: {}", productTags);

        searchService.removeTagCache(productTags.getProductId(), productTags.getTagsList());
    }

}
