package com.coffeeshop.app.modules.order.dto;

import com.coffeeshop.app.modules.product.entity.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private Long productVariantId;
    private String productName;
    private Size size;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private String note;
    private BigDecimal subtotal;
    private List<OrderItemToppingResponse> toppings;
}
