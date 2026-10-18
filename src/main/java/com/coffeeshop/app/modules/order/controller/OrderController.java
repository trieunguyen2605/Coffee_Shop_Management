package com.coffeeshop.app.modules.order.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.order.dto.*;
import com.coffeeshop.app.modules.order.service.OrderService;
import com.coffeeshop.app.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(userDetails.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Mở đơn hàng mới thành công", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn hàng thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chi tiết đơn hàng thành công", response));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<OrderResponse>> addItemsToOrder(
            @PathVariable Long id,
            @Valid @RequestBody AddItemsToOrderRequest request) {
        OrderResponse response = orderService.addItemsToOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm món vào đơn hàng thành công", response));
    }

    @PutMapping("/{id}/move-table")
    public ResponseEntity<ApiResponse<OrderResponse>> moveTable(
            @PathVariable Long id,
            @Valid @RequestBody MoveTableRequest request) {
        OrderResponse response = orderService.moveTable(id, request);
        return ResponseEntity.ok(ApiResponse.success("Chuyển bàn thành công", response));
    }

    @PostMapping("/merge-table")
    public ResponseEntity<ApiResponse<OrderResponse>> mergeTable(
            @Valid @RequestBody MergeTableRequest request) {
        OrderResponse response = orderService.mergeTable(request);
        return ResponseEntity.ok(ApiResponse.success("Gộp bàn thành công", response));
    }
}
