package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest inventoryRequest);
    List<InventoryResponse> getAllInventories();
    InventoryResponse getInventoryById(Long id);
    InventoryResponse getInventoryByProductId(Long productId);
    InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest);
    void deleteInventory(Long id);
}