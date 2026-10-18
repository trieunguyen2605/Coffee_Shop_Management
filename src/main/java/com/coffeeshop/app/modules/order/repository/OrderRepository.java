package com.coffeeshop.app.modules.order.repository;

import com.coffeeshop.app.modules.order.entity.Order;
import com.coffeeshop.app.modules.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByTableIdAndOrderStatus(Long tableId, OrderStatus status);

    boolean existsByTableIdAndOrderStatus(Long tableId, OrderStatus status);

    List<Order> findAllByOrderByCreatedAtDesc();

    // JOIN FETCH Order kèm Table, User, Items, ProductVariant, Product (1 collection duy nhất tránh MultipleBagFetchException)
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.table " +
           "LEFT JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.productVariant pv " +
           "LEFT JOIN FETCH pv.product " +
           "WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
}
