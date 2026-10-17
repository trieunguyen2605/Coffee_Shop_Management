package com.coffeeshop.app.modules.inventory.service;

import com.coffeeshop.app.common.exception.ResourceNotFoundException;
import com.coffeeshop.app.modules.inventory.dto.RecipeItemRequest;
import com.coffeeshop.app.modules.inventory.dto.RecipeResponse;
import com.coffeeshop.app.modules.inventory.dto.SetRecipeRequest;
import com.coffeeshop.app.modules.inventory.entity.Ingredient;
import com.coffeeshop.app.modules.inventory.entity.Recipe;
import com.coffeeshop.app.modules.inventory.repository.IngredientRepository;
import com.coffeeshop.app.modules.inventory.repository.RecipeRepository;
import com.coffeeshop.app.modules.product.entity.ProductVariant;
import com.coffeeshop.app.modules.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public List<RecipeResponse> setRecipeForVariant(SetRecipeRequest request) {
        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + request.getProductVariantId()));

        // Xóa công thức cũ
        recipeRepository.deleteByProductVariantId(variant.getId());

        List<Recipe> recipesToSave = new ArrayList<>();
        for (RecipeItemRequest item : request.getItems()) {
            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nguyên liệu với ID: " + item.getIngredientId()));
            
            Recipe recipe = Recipe.builder()
                    .productVariant(variant)
                    .ingredient(ingredient)
                    .amountRequired(item.getAmountRequired())
                    .build();
            recipesToSave.add(recipe);
        }

        List<Recipe> savedRecipes = recipeRepository.saveAll(recipesToSave);
        return savedRecipes.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipeByVariantId(Long variantId) {
        if (!productVariantRepository.existsById(variantId)) {
            throw new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm với ID: " + variantId);
        }
        return recipeRepository.findByProductVariantId(variantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setProductVariantId(recipe.getProductVariant().getId());
        response.setProductName(recipe.getProductVariant().getProduct().getName());
        response.setSize(recipe.getProductVariant().getSize());
        response.setIngredientId(recipe.getIngredient().getId());
        response.setIngredientName(recipe.getIngredient().getName());
        response.setUnit(recipe.getIngredient().getUnit());
        response.setAmountRequired(recipe.getAmountRequired());
        return response;
    }
}
