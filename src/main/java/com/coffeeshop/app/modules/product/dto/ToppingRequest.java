package com.coffeeshop.app.modules.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ToppingRequest {

    @NotBlank(message = "Tên Topping không được để trống")
    private String name;

    @NotNull(message = "Giá tiền không được để trống")
    @PositiveOrZero(message = "Giá tiền không được là số âm")
    private BigDecimal price;
}
