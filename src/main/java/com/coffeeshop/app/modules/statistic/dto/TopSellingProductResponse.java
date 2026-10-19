package com.coffeeshop.app.modules.statistic.dto;

import com.coffeeshop.app.modules.product.entity.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductResponse {
    private Long productVariantId;
    private String productName;
    private Size size;
    private Long totalQuantitySold;
    private BigDecimal totalSalesAmount;
}
