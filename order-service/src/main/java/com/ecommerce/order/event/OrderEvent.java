package com.ecommerce.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private String status;
}