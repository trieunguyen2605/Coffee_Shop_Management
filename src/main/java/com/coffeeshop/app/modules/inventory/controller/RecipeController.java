package com.coffeeshop.app.modules.inventory.controller;

import com.coffeeshop.app.common.response.ApiResponse;
import com.coffeeshop.app.modules.inventory.dto.RecipeResponse;
import com.coffeeshop.app.modules.inventory.dto.SetRecipeRequest;
import com.coffeeshop.app.modules.inventory.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> setRecipe(@Valid @RequestBody SetRecipeRequest request) {
        List<RecipeResponse> responses = recipeService.setRecipeForVariant(request);
        return ResponseEntity.ok(ApiResponse.success("Thiết lập công thức thành công", responses));
    }

    @GetMapping("/variant/{variantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<RecipeResponse>>> getRecipeByVariantId(@PathVariable Long variantId) {
        List<RecipeResponse> responses = recipeService.getRecipeByVariantId(variantId);
        return ResponseEntity.ok(ApiResponse.success("Lấy công thức thành công", responses));
    }
}
