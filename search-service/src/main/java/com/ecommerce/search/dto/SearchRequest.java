package com.ecommerce.search.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String query; // Search query (e.g., product name or description)
}