package com.ecommerce.order.service.impl;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.event.OrderEvent;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setStatus(orderRequest.getStatus());
        Order savedOrder = orderRepository.save(order);

        // Publish order event to Kafka
        OrderEvent orderEvent = new OrderEvent(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getProductId(),
                savedOrder.getQuantity(), savedOrder.getTotalPrice(), savedOrder.getStatus());
        kafkaTemplate.send("order-topic", String.valueOf(savedOrder.getId()), orderEvent);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setUserId(orderRequest.getUserId());
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setStatus(orderRequest.getStatus());
        Order updatedOrder = orderRepository.save(order);

        // Publish order event to Kafka
        OrderEvent orderEvent = new OrderEvent(updatedOrder.getId(), updatedOrder.getUserId(), updatedOrder.getProductId(),
                updatedOrder.getQuantity(), updatedOrder.getTotalPrice(), updatedOrder.getStatus());
        kafkaTemplate.send("order-topic", String.valueOf(updatedOrder.getId()), orderEvent);

        return mapToOrderResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);

        // Publish order deletion event to Kafka
        OrderEvent orderEvent = new OrderEvent(id, null, null, null, null, "DELETED");
        kafkaTemplate.send("order-topic", String.valueOf(id), orderEvent);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        return response;
    }
}