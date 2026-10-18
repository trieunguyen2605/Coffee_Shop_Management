package com.coffeeshop.app.modules.order.entity;

import com.coffeeshop.app.common.entity.BaseEntity;
import com.coffeeshop.app.modules.product.entity.Topping;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item_toppings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemTopping extends BaseEntity {

    // Snapshot giá topping tại thời điểm gọi
    @Column(nullable = false)
    private BigDecimal toppingPriceAtOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topping_id", nullable = false)
    private Topping topping;
}
