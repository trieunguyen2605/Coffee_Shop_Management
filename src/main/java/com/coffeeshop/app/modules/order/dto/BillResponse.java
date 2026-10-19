package com.coffeeshop.app.modules.order.dto;

import com.coffeeshop.app.modules.order.entity.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BillResponse {

    private String orderCode;
    private String tableName;
    private String cashierName;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private PaymentMethod paymentMethod;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
