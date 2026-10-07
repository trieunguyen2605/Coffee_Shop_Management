package com.coffeeshop.app.modules.product.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.product.dto.ProductCreateRequest;
import com.coffeeshop.app.modules.product.dto.ProductResponse;
import com.coffeeshop.app.modules.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo sản phẩm thành công", response), HttpStatus.CREATED);
    }

    @GetMapping("/menu")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMenu() {
        List<ProductResponse> responses = productService.getMenu();
        return ResponseEntity.ok(ApiResponse.success("Lấy menu bán hàng thành công", responses));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        
        ProductResponse response = productService.updateProductStatus(id, isActive);
        String message = isActive ? "Đã mở bán sản phẩm" : "Đã ngừng bán sản phẩm";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<ProductResponse> responses = productService.getAllProducts(categoryId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tất cả sản phẩm thành công", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody com.coffeeshop.app.modules.product.dto.ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công", response));
    }
}
