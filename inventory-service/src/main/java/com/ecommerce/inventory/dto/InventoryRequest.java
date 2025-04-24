package com.ecommerce.inventory.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private Long productId;
    private Integer stock;
}