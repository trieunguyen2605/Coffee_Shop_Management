package com.coffeeshop.app.modules.inventory.service;

public interface InventoryService {
    void checkAndDeductStock(Long productVariantId, int quantity);
}
