package com.coffeeshop.app.modules.inventory.service;

import com.coffeeshop.app.common.exception.AppException;
import com.coffeeshop.app.modules.inventory.entity.Ingredient;
import com.coffeeshop.app.modules.inventory.entity.Recipe;
import com.coffeeshop.app.modules.inventory.repository.IngredientRepository;
import com.coffeeshop.app.modules.inventory.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional
    public void checkAndDeductStock(Long productVariantId, int quantity) {
        List<Recipe> recipes = recipeRepository.findByProductVariantId(productVariantId);
        
        for (Recipe recipe : recipes) {
            Ingredient ingredient = recipe.getIngredient();
            BigDecimal totalRequired = recipe.getAmountRequired().multiply(BigDecimal.valueOf(quantity));
            
            if (ingredient.getCurrentStock().compareTo(totalRequired) < 0) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Nguyên liệu " + ingredient.getName() + " không đủ tồn kho");
            }
            
            ingredient.setCurrentStock(ingredient.getCurrentStock().subtract(totalRequired));
            ingredientRepository.save(ingredient);
        }
    }
}
