package com.coffeeshop.app.modules.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "ID Danh mục không được để trống")
    private Long categoryId;

    @NotEmpty(message = "Sản phẩm phải có ít nhất 1 biến thể size")
    @Valid
    private List<VariantRequest> variants;
}
