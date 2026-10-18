package com.coffeeshop.app.modules.order.entity;

public enum OrderStatus {
    PENDING,    // Đang phục vụ
    COMPLETED,  // Đã thanh toán
    CANCELLED   // Đã hủy / Gộp sang đơn khác
}
