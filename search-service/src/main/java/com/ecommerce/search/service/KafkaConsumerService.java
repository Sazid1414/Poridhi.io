package com.ecommerce.search.service;

import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.event.ProductEvent;
import com.ecommerce.search.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final SearchRepository searchRepository;

    public KafkaConsumerService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @KafkaListener(topics = "product-topic", groupId = "search-group",
            containerFactory = "productEventKafkaListenerContainerFactory")
    public void consumeProductEvent(ProductEvent productEvent) {
        logger.info("Received ProductEvent: {}", productEvent);
        try {
            if ("CREATED".equalsIgnoreCase(productEvent.getAction()) || "UPDATED".equalsIgnoreCase(productEvent.getAction())) {
                SearchResponse searchResponse = new SearchResponse();
                searchResponse.setId(productEvent.getProductId());
                searchResponse.setName(productEvent.getName());
                searchResponse.setDescription(productEvent.getDescription());
                searchResponse.setPrice(productEvent.getPrice());
                searchResponse.setStock(productEvent.getStock());
                searchRepository.save(searchResponse);
                logger.info("Indexed product in Elasticsearch: {}", searchResponse);
            } else if ("DELETED".equalsIgnoreCase(productEvent.getAction())) {
                searchRepository.deleteById(productEvent.getProductId());
                logger.info("Deleted product from Elasticsearch: {}", productEvent.getProductId());
            }
        } catch (Exception e) {
            logger.error("Error processing ProductEvent: {}", productEvent, e);
            throw e;
        }
    }
}