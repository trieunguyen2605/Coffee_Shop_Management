package com.coffeeshop.app.modules.statistic.repository;

import com.coffeeshop.app.modules.order.entity.Order;
import com.coffeeshop.app.modules.statistic.repository.projection.DailyRevenueProjection;
import com.coffeeshop.app.modules.statistic.repository.projection.TopSellingProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT " +
                   "DATE(COALESCE(o.paid_at, o.created_at)) AS orderDate, " +
                   "COALESCE(SUM(o.final_amount), 0) AS totalRevenue, " +
                   "COUNT(o.id) AS totalOrders " +
                   "FROM orders o " +
                   "WHERE o.order_status = 'COMPLETED' " +
                   "AND COALESCE(o.paid_at, o.created_at) BETWEEN :startDateTime AND :endDateTime " +
                   "GROUP BY DATE(COALESCE(o.paid_at, o.created_at)) " +
                   "ORDER BY orderDate ASC", nativeQuery = true)
    List<DailyRevenueProjection> getDailyRevenueBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query(value = "SELECT " +
                   "pv.id AS productVariantId, " +
                   "p.name AS productName, " +
                   "pv.size AS size, " +
                   "CAST(COALESCE(SUM(oi.quantity), 0) AS SIGNED) AS totalQuantitySold, " +
                   "COALESCE(SUM(oi.subtotal), 0) AS totalSalesAmount " +
                   "FROM order_items oi " +
                   "JOIN orders o ON oi.order_id = o.id " +
                   "JOIN product_variants pv ON oi.product_variant_id = pv.id " +
                   "JOIN products p ON pv.product_id = p.id " +
                   "WHERE o.order_status = 'COMPLETED' " +
                   "GROUP BY pv.id, p.name, pv.size " +
                   "ORDER BY totalQuantitySold DESC, totalSalesAmount DESC", nativeQuery = true)
    List<TopSellingProjection> getTopSellingProducts(Pageable pageable);
}
