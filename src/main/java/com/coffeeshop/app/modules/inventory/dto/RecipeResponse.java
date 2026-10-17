package com.coffeeshop.app.modules.inventory.dto;

import com.coffeeshop.app.modules.inventory.entity.Unit;
import com.coffeeshop.app.modules.product.entity.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecipeResponse {
    private Long id;
    private Long productVariantId;
    private String productName;
    private Size size;
    private Long ingredientId;
    private String ingredientName;
    private Unit unit;
    private BigDecimal amountRequired;
}
