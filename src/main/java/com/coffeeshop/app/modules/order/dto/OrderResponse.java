package com.coffeeshop.app.modules.order.dto;

import com.coffeeshop.app.modules.order.entity.OrderStatus;
import com.coffeeshop.app.modules.order.entity.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String orderCode;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private Long tableId;
    private String tableName;
    private Long userId;
    private String creatorName;
    private BigDecimal totalAmount;
    private Integer discountPercentage;
    private BigDecimal finalAmount;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
