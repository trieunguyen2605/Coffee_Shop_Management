package com.coffeeshop.app.modules.statistic.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal overallRevenue;
    private Long overallOrders;
    private List<DailyRevenueResponse> dailyRevenues;
}
