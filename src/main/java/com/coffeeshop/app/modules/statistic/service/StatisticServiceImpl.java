package com.coffeeshop.app.modules.statistic.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.product.entity.Size;
import com.coffeeshop.app.modules.statistic.dto.DailyRevenueResponse;
import com.coffeeshop.app.modules.statistic.dto.RevenueStatisticResponse;
import com.coffeeshop.app.modules.statistic.dto.TopSellingProductResponse;
import com.coffeeshop.app.modules.statistic.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    @Transactional(readOnly = true)
    public RevenueStatisticResponse getRevenueStatistic(LocalDate startDate, LocalDate endDate) {
        // Mặc định nếu không truyền: từ đầu tháng hiện tại đến ngày hôm nay
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        if (start.isAfter(end)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu (startDate) không được lớn hơn ngày kết thúc (endDate)!");
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        List<DailyRevenueResponse> dailyRevenues = statisticRepository.getDailyRevenueBetween(startDateTime, endDateTime).stream()
                .map(proj -> DailyRevenueResponse.builder()
                        .date(proj.getOrderDate())
                        .totalRevenue(proj.getTotalRevenue() != null ? proj.getTotalRevenue() : BigDecimal.ZERO)
                        .totalOrders(proj.getTotalOrders() != null ? proj.getTotalOrders() : 0L)
                        .build())
                .toList();

        BigDecimal overallRevenue = dailyRevenues.stream()
                .map(DailyRevenueResponse::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long overallOrders = dailyRevenues.stream()
                .map(DailyRevenueResponse::getTotalOrders)
                .reduce(0L, Long::sum);

        return RevenueStatisticResponse.builder()
                .startDate(start)
                .endDate(end)
                .overallRevenue(overallRevenue)
                .overallOrders(overallOrders)
                .dailyRevenues(dailyRevenues)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        int safeLimit = (limit <= 0) ? 5 : limit;
        return statisticRepository.getTopSellingProducts(PageRequest.of(0, safeLimit)).stream()
                .map(proj -> {
                    Size size = null;
                    if (proj.getSize() != null) {
                        try {
                            size = Size.valueOf(proj.getSize());
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    return TopSellingProductResponse.builder()
                            .productVariantId(proj.getProductVariantId())
                            .productName(proj.getProductName())
                            .size(size)
                            .totalQuantitySold(proj.getTotalQuantitySold() != null ? proj.getTotalQuantitySold() : 0L)
                            .totalSalesAmount(proj.getTotalSalesAmount() != null ? proj.getTotalSalesAmount() : BigDecimal.ZERO)
                            .build();
                })
                .toList();
    }
}
