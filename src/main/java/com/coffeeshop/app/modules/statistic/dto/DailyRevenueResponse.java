package com.coffeeshop.app.modules.statistic.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRevenueResponse {
    private LocalDate date;
    private BigDecimal totalRevenue;
    private Long totalOrders;
}
