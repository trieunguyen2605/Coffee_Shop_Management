package com.coffeeshop.app.modules.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemToppingResponse {
    private Long id;
    private Long toppingId;
    private String toppingName;
    private BigDecimal toppingPriceAtOrder;
}
