package com.coffeeshop.app.modules.order.service;

import com.coffeeshop.app.modules.order.dto.*;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long currentUserId, CreateOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllOrders();
    OrderResponse addItemsToOrder(Long orderId, AddItemsToOrderRequest request);
    OrderResponse moveTable(Long orderId, MoveTableRequest request);
    OrderResponse mergeTable(MergeTableRequest request);
}
