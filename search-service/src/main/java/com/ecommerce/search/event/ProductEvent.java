package com.ecommerce.search.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String action; // e.g., CREATED, UPDATED, DELETED
}