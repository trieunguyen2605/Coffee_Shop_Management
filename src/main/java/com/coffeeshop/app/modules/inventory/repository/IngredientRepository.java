package com.coffeeshop.app.modules.inventory.repository;

import com.coffeeshop.app.modules.inventory.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    boolean existsByName(String name);
    
    @Query("SELECT i FROM Ingredient i WHERE i.currentStock <= i.minStockAlert")
    List<Ingredient> findLowStockIngredients();
}
