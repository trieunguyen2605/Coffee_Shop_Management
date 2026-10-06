package com.coffeeshop.app.modules.table.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AreaResponse {
    
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
