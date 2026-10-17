package com.coffeeshop.app.modules.inventory.dto;

import com.coffeeshop.app.modules.inventory.entity.Unit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IngredientResponse {
    private Long id;
    private String name;
    private Unit unit;
    private BigDecimal currentStock;
    private BigDecimal minStockAlert;
    
    public boolean getIsLowStock() {
        if (currentStock == null || minStockAlert == null) return false;
        return currentStock.compareTo(minStockAlert) <= 0;
    }
}
