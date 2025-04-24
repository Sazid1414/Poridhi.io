package com.ecommerce.inventory.dto;

import lombok.Data;

@Data
public class InventoryResponse {
    private Long id;
    private Long productId;
    private Integer stock;
}