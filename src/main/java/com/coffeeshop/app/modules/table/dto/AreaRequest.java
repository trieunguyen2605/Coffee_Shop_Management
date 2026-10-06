package com.coffeeshop.app.modules.table.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaRequest {

    @NotBlank(message = "Tên khu vực không được để trống")
    private String name;
}
