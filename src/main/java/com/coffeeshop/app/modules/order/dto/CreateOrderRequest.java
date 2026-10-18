package com.coffeeshop.app.modules.order.dto;

import com.coffeeshop.app.modules.order.entity.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull(message = "Loại đơn hàng không được để trống")
    private OrderType orderType;

    // Bắt buộc nếu orderType == DINE_IN, được kiểm tra trong Service
    private Long tableId;

    private String note;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 món")
    @Valid
    private List<OrderItemRequest> items;
}
