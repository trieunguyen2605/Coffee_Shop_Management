package com.coffeeshop.app.modules.statistic.service;

import com.coffeeshop.app.modules.statistic.dto.RevenueStatisticResponse;
import com.coffeeshop.app.modules.statistic.dto.TopSellingProductResponse;

import java.time.LocalDate;
import java.util.List;

public interface StatisticService {

    RevenueStatisticResponse getRevenueStatistic(LocalDate startDate, LocalDate endDate);

    List<TopSellingProductResponse> getTopSellingProducts(int limit);
}
