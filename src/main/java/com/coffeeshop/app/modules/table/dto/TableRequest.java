package com.coffeeshop.app.modules.table.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableRequest {

    @NotBlank(message = "Tên bàn không được để trống")
    private String name;

    @NotNull(message = "ID khu vực không được để trống")
    private Long areaId;
}
