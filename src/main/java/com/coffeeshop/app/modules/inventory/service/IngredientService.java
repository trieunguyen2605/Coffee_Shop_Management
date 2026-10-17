package com.coffeeshop.app.modules.inventory.service;

import com.coffeeshop.app.modules.inventory.dto.IngredientImportRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientRequest;
import com.coffeeshop.app.modules.inventory.dto.IngredientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientService {
    IngredientResponse createIngredient(IngredientRequest request);
    IngredientResponse importStock(Long id, IngredientImportRequest request);
    Page<IngredientResponse> getAllIngredients(Pageable pageable);
    List<IngredientResponse> getLowStockIngredients();
}
