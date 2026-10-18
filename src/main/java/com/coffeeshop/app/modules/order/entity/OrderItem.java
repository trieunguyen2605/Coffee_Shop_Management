package com.coffeeshop.app.modules.order.entity;

import com.coffeeshop.app.common.entity.BaseEntity;
import com.coffeeshop.app.modules.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Column(nullable = false)
    private Integer quantity;

    // Snapshot giá tại thời điểm gọi món (cực kỳ quan trọng)
    @Column(nullable = false)
    private BigDecimal priceAtOrder;

    private String note;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @org.hibernate.annotations.BatchSize(size = 30)
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemTopping> toppings = new ArrayList<>();
}
