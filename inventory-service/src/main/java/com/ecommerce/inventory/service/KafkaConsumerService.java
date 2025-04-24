package com.ecommerce.inventory.service;

import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.event.OrderEvent;
import com.ecommerce.inventory.event.ProductEvent;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    public KafkaConsumerService(InventoryRepository inventoryRepository, InventoryService inventoryService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-topic", groupId = "inventory-group",
            containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEvent orderEvent) {
        logger.info("Received OrderEvent: {}", orderEvent);
        try {
            if ("CREATED".equalsIgnoreCase(orderEvent.getAction())) {
                Long productId = orderEvent.getProductId();
                Integer quantity = orderEvent.getQuantity();
                Inventory inventory = inventoryRepository.findByProductId(productId)
                        .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
                if (inventory.getStock() < quantity) {
                    throw new RuntimeException("Insufficient stock for product: " + productId);
                }
                inventory.setStock(inventory.getStock() - quantity);
                inventoryRepository.save(inventory);
                logger.info("Reduced stock for product {} by {} units. New stock: {}", productId, quantity, inventory.getStock());
            }
        } catch (Exception e) {
            logger.error("Error processing OrderEvent: {}", orderEvent, e);
            // In a production system, consider sending to a dead-letter queue
            throw e;
        }
    }

    @KafkaListener(topics = "product-topic", groupId = "inventory-group",
            containerFactory = "productEventKafkaListenerContainerFactory")
    public void consumeProductEvent(ProductEvent productEvent) {
        logger.info("Received ProductEvent: {}", productEvent);
        try {
            if ("CREATED".equalsIgnoreCase(productEvent.getAction())) {
                Inventory inventory = new Inventory();
                inventory.setProductId(productEvent.getProductId());
                inventory.setStock(productEvent.getStock());
                inventoryRepository.save(inventory);
                logger.info("Initialized inventory for product {} with stock: {}", productEvent.getProductId(), productEvent.getStock());
            } else if ("DELETED".equalsIgnoreCase(productEvent.getAction())) {
                inventoryRepository.deleteByProductId(productEvent.getProductId());
                logger.info("Deleted inventory for product: {}", productEvent.getProductId());
            }
        } catch (Exception e) {
            logger.error("Error processing ProductEvent: {}", productEvent, e);
            // In a production system, consider sending to a dead-letter queue
            throw e;
        }
    }
}