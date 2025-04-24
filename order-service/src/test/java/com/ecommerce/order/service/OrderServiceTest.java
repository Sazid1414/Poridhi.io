package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.event.OrderEvent;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        order = new Order(1L, 1L, 1L, 2, 1999.98, "PLACED");
        orderRequest = new OrderRequest(1L, 1L, 2, 1999.98, "PLACED");
    }

    @Test
    void createOrder_ReturnsCreatedOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEvent.class))).thenReturn(null);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PLACED", response.getStatus());
        verify(kafkaTemplate, times(1)).send(eq("order-topic"), anyString(), any(OrderEvent.class));
    }

    @Test
    void getAllOrders_ReturnsOrderList() {
        List<Order> orders = Arrays.asList(
                new Order(1L, 1L, 1L, 2, 1999.98, "PLACED"),
                new Order(2L, 2L, 2L, 1, 599.99, "CONFIRMED")
        );
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponse> responses = orderService.getAllOrders();

        assertEquals(2, responses.size());
        assertEquals("PLACED", responses.get(0).getStatus());
        assertEquals("CONFIRMED", responses.get(1).getStatus());
    }

    @Test
    void getOrderById_ReturnsOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PLACED", response.getStatus());
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void updateOrder_ReturnsUpdatedOrder() {
        OrderRequest updateRequest = new OrderRequest(1L, 1L, 3, 2999.97, "CONFIRMED");
        Order updatedOrder = new Order(1L, 1L, 1L, 3, 2999.97, "CONFIRMED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEvent.class))).thenReturn(null);

        OrderResponse response = orderService.updateOrder(1L, updateRequest);

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(2999.97, response.getTotalPrice());
        verify(kafkaTemplate, times(1)).send(eq("order-topic"), anyString(), any(OrderEvent.class));
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderEvent.class))).thenReturn(null);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
        verify(kafkaTemplate, times(1)).send(eq("order-topic"), anyString(), any(OrderEvent.class));
    }

    @Test
    void deleteOrder_NotFound_ThrowsException() {
        when(orderRepository.existsById(999L)).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(999L));
    }
}