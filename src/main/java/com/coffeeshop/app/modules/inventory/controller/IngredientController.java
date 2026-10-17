package com.coffeeshop.app.modules.inventory.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.inventory.dto.IngredientImportRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientResponse;
import com.coffeeshop.app.modules.inventory.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<IngredientResponse>> createIngredient(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.createIngredient(request);
        return new ResponseEntity<>(ApiResponse.success("Tạo nguyên liệu thành công", response), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<IngredientResponse>> importStock(
            @PathVariable Long id,
            @Valid @RequestBody IngredientImportRequest request) {
        IngredientResponse response = ingredientService.importStock(id, request);
        return ResponseEntity.ok(ApiResponse.success("Nhập kho thành công", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<IngredientResponse>>> getAllIngredients(Pageable pageable) {
        Page<IngredientResponse> responses = ingredientService.getAllIngredients(pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nguyên liệu thành công", responses));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getLowStockIngredients() {
        List<IngredientResponse> responses = ingredientService.getLowStockIngredients();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nguyên liệu sắp hết thành công", responses));
    }
}
