package com.coffeeshop.app.modules.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecipeItemRequest {
    @NotNull(message = "ID nguyên liệu không được để trống")
    private Long ingredientId;

    @NotNull(message = "Định lượng không được để trống")
    @Positive(message = "Định lượng phải lớn hơn 0")
    private BigDecimal amountRequired;
}
