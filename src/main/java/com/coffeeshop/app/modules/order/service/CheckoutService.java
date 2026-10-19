package com.coffeeshop.app.modules.order.service;

import com.coffeeshop.app.modules.order.dto.BillResponse;
import com.coffeeshop.app.modules.order.dto.CheckoutRequest;

public interface CheckoutService {

    BillResponse checkoutOrder(Long orderId, Long cashierUserId, CheckoutRequest request);
}
