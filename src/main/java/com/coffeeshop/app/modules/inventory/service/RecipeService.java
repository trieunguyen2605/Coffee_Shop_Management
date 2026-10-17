package com.coffeeshop.app.modules.inventory.service;

import com.coffeeshop.app.modules.inventory.dto.RecipeResponse;
import com.coffeeshop.app.modules.inventory.dto.SetRecipeRequest;

import java.util.List;

public interface RecipeService {
    List<RecipeResponse> setRecipeForVariant(SetRecipeRequest request);
    List<RecipeResponse> getRecipeByVariantId(Long variantId);
}
