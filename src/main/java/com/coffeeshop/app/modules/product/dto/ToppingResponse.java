package com.coffeeshop.app.modules.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ToppingResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private boolean isActive;
}
