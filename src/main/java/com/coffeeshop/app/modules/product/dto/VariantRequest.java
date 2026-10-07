package com.coffeeshop.app.modules.product.dto;

import com.coffeeshop.app.modules.product.entity.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VariantRequest {

    @NotNull(message = "Kích cỡ (Size) không được để trống")
    private Size size;

    @NotNull(message = "Giá tiền không được để trống")
    @PositiveOrZero(message = "Giá tiền không được là số âm")
    private BigDecimal price;
}
