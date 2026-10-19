package com.coffeeshop.app.modules.statistic.repository.projection;

import java.math.BigDecimal;

public interface TopSellingProjection {
    Long getProductVariantId();
    String getProductName();
    String getSize();
    Long getTotalQuantitySold();
    BigDecimal getTotalSalesAmount();
}
