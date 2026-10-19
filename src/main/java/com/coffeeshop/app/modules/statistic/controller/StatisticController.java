package com.coffeeshop.app.modules.statistic.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.statistic.dto.RevenueStatisticResponse;
import com.coffeeshop.app.modules.statistic.dto.TopSellingProductResponse;
import com.coffeeshop.app.modules.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueStatisticResponse>> getRevenueStatistic(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        RevenueStatisticResponse response = statisticService.getRevenueStatistic(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê doanh thu thành công", response));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<ApiResponse<List<TopSellingProductResponse>>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        List<TopSellingProductResponse> response = statisticService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sản phẩm bán chạy thành công", response));
    }
}
