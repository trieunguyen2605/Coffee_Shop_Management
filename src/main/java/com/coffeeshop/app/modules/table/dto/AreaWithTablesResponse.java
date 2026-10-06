package com.coffeeshop.app.modules.table.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AreaWithTablesResponse {
    
    private Long id;
    private String name;
    private List<TableResponse> tables;
}
