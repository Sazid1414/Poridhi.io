package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveAndFindOrder() {
        Order order = new Order(null, 1L, 1L, 2, 1999.98, "PLACED");
        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals("PLACED", savedOrder.getStatus());

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertTrue(foundOrder.isPresent());
        assertEquals("PLACED", foundOrder.get().getStatus());
    }

    @Test
    void deleteOrder() {
        Order order = new Order(null, 1L, 1L, 2, 1999.98, "PLACED");
        Order savedOrder = orderRepository.save(order);

        orderRepository.deleteById(savedOrder.getId());

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertFalse(foundOrder.isPresent());
    }
}