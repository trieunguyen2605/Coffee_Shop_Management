package com.coffeeshop.app.modules.inventory.dto;

import com.coffeeshop.app.modules.inventory.entity.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IngredientRequest {

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    private String name;

    @NotNull(message = "Đơn vị tính không được để trống")
    private Unit unit;

    @NotNull(message = "Tồn kho không được để trống")
    @PositiveOrZero(message = "Tồn kho phải lớn hơn hoặc bằng 0")
    private BigDecimal currentStock;

    @PositiveOrZero(message = "Ngưỡng cảnh báo phải lớn hơn hoặc bằng 0")
    private BigDecimal minStockAlert;
}
