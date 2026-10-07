package com.coffeeshop.app.modules.product.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.product.dto.ToppingRequest;
import com.coffeeshop.app.modules.product.dto.ToppingResponse;
import com.coffeeshop.app.modules.product.service.ToppingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/toppings")
@RequiredArgsConstructor
public class ToppingController {

    private final ToppingService toppingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ToppingResponse>> createTopping(@Valid @RequestBody ToppingRequest request) {
        ToppingResponse response = toppingService.createTopping(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo Topping thành công", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ToppingResponse>>> getActiveToppings() {
        List<ToppingResponse> responses = toppingService.getActiveToppings();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách Topping thành công", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ToppingResponse>> updateTopping(
            @PathVariable Long id, 
            @Valid @RequestBody ToppingRequest request) {
        ToppingResponse response = toppingService.updateTopping(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật Topping thành công", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ToppingResponse>> updateToppingStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        ToppingResponse response = toppingService.updateToppingStatus(id, isActive);
        String message = isActive ? "Đã mở bán Topping" : "Đã ngừng bán Topping";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }
}
