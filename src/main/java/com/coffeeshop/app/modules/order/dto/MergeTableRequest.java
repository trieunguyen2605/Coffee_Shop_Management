package com.coffeeshop.app.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MergeTableRequest {
    @NotNull(message = "ID bàn nguồn không được để trống")
    private Long fromTableId;

    @NotNull(message = "ID bàn đích không được để trống")
    private Long toTableId;
}
