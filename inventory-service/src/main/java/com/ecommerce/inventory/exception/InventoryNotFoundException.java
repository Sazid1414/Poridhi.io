package com.ecommerce.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(Long id) {
        super("Inventory not found with id: " + id);
    }

    public InventoryNotFoundException(Long productId, String message) {
        super("Inventory not found for product id: " + productId + ". " + message);
    }
}