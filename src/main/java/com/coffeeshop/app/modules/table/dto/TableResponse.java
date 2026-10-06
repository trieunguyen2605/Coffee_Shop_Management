package com.coffeeshop.app.modules.table.dto;

import com.coffeeshop.app.modules.table.entity.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableResponse {
    
    private Long id;
    private String name;
    private TableStatus status;
    private Long areaId;
    private String areaName;
}
