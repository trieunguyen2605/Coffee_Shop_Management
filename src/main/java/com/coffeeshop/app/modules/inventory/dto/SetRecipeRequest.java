package com.coffeeshop.app.modules.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetRecipeRequest {
    @NotNull(message = "ID biến thể sản phẩm không được để trống")
    private Long productVariantId;

    @NotEmpty(message = "Công thức không được rỗng")
    @Valid
    private List<RecipeItemRequest> items;
}
