package com.coffeeshop.app.modules.product.dto;

import com.coffeeshop.app.modules.product.entity.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VariantResponse {

    private Long id;
    private Size size;
    private BigDecimal price;
    private boolean isActive;
}
