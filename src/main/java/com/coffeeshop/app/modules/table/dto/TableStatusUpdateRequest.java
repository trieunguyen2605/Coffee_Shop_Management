package com.coffeeshop.app.modules.table.dto;

import com.coffeeshop.app.modules.table.entity.TableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableStatusUpdateRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private TableStatus status;
}
